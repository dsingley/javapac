package com.dsingley.proxyautoconfiguration;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Suppliers;
import com.google.common.cache.AbstractLoadingCache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class ProxyAutoConfiguration {
    private static final String PAC_FILE_URL = "PAC_FILE_URL";
    private static final ScriptEngineManager SCRIPT_ENGINE_MANAGER = new ScriptEngineManager();
    private static final String SCRIPT_ENGINE_NAME = "nashorn";
    private static final String JS_FUNCTION_NAME = "FindProxyForURL";

    private final String dnsResolveJsFunction;
    private final String pacJsFunctions;
    private final Supplier<String> pacFileSupplier;
    private final LoadingCache<CacheKey, String> loadingCache;

    public static void main(String[] args) throws Exception {
        String pacFileUrl = System.getenv(PAC_FILE_URL);
        if (pacFileUrl == null || pacFileUrl.trim().length() == 0) {
            System.err.printf("ERROR: %s environment variable is not set%n", PAC_FILE_URL);
            System.exit(1);
        }
        if (args.length < 1) {
            System.err.printf("ERROR: one or more URL command line arguments are required%n");
            System.exit(2);
        }
        ProxyAutoConfiguration pac = new ProxyAutoConfiguration(new URL(pacFileUrl), 1, TimeUnit.HOURS);
        for (String arg : args) {
            System.out.printf("%s --> %s%n", arg, pac.findProxyForURL(arg));
        }
    }

    @SneakyThrows
    public ProxyAutoConfiguration(URL pacFileUrl, long duration, TimeUnit timeUnit) {
        dnsResolveJsFunction = generateDnsResolveJsFunction();
        pacJsFunctions = loadPacJsFunctions();
        pacFileSupplier = Suppliers.memoizeWithExpiration(() -> loadPacFile(pacFileUrl), duration, timeUnit);
        loadingCache = CacheBuilder.newBuilder()
                .expireAfterWrite(duration, timeUnit)
                .build(new CacheLoader<CacheKey, String>() {
                    @Override
                    public String load(@NonNull CacheKey key) {
                        return findProxyForURL(key);
                    }
                });
    }

    @VisibleForTesting
    ProxyAutoConfiguration(String pacFile) {
        dnsResolveJsFunction = generateDnsResolveJsFunction();
        pacJsFunctions = loadPacJsFunctions();
        pacFileSupplier = () -> pacFile;
        loadingCache = new NonCachingLoadingCache<>(this::findProxyForURL);
    }

    @SneakyThrows
    public String findProxyForURL(String url) {
        return findProxyForURL(new URL(url));
    }

    public String findProxyForURL(URL url) {
        return findProxyForURL(url.toString(), url.getHost());
    }

    @SneakyThrows
    public String findProxyForURL(String url, String host) {
        log.info("{}('{}', '{}')", JS_FUNCTION_NAME, url, host);
        return loadingCache.get(new CacheKey(url, host));
    }

    @SneakyThrows
    private String findProxyForURL(CacheKey cacheKey) {
        String url = cacheKey.getUrl();
        String host = cacheKey.getHost();
        log.info("evaluating {}('{}', '{}')", JS_FUNCTION_NAME, url, host);
        String script = String.join("\n", Arrays.asList(
                dnsResolveJsFunction,
                pacJsFunctions,
                pacFileSupplier.get()
        ));
        ScriptEngine scriptEngine = SCRIPT_ENGINE_MANAGER.getEngineByName(SCRIPT_ENGINE_NAME);
        try {
            scriptEngine.eval(script);
        } catch (ScriptException e) {
            log.error("exception evaluating script:\n{}", prependLineNumbers(script));
            throw e;
        }
        try {
            return (String) ((Invocable) scriptEngine).invokeFunction(JS_FUNCTION_NAME, url, host);
        } catch (ScriptException | NoSuchMethodException e) {
            log.error("exception invoking {} function; script:\n{}", JS_FUNCTION_NAME, prependLineNumbers(script));
            throw e;
        }
    }

    public static String dnsResolve(String host) {
        String ipAddress;
        try {
            ipAddress = InetAddress.getByName(host).getHostAddress();
        } catch (UnknownHostException e) {
            ipAddress = null;
        }
        log.info("dnsResolve('{}') --> '{}'", host, ipAddress);
        return ipAddress;
    }

    private static String generateDnsResolveJsFunction() {
        return String.join("\n", Arrays.asList(
                "function dnsResolve(host) {",
                "    var javaClass = Java.type('" + ProxyAutoConfiguration.class.getName() + "');",
                "    return javaClass.dnsResolve(host);",
                "}"
        ));
    }

    private static String loadPacJsFunctions() {
        return read("/pac_utils.js");
    }

    @SneakyThrows
    private static String loadPacFile(URL pacFileUrl) {
        String protocol = pacFileUrl.getProtocol();
        switch (protocol) {
            case "file":
                try (InputStream inputStream = Files.newInputStream(Paths.get(pacFileUrl.getPath()))) {
                    return read(inputStream);
                }
            case "http":
            case "https":
                URLConnection urlConnection = pacFileUrl.openConnection();
                try (InputStream inputStream = urlConnection.getInputStream()) {
                    return read(inputStream);
                }
            default:
                throw new IllegalArgumentException(String.format("unsupported protocol for PAC file URL: %s", protocol));
        }
    }

    @VisibleForTesting
    static String read(String name) {
        return read(ProxyAutoConfiguration.class.getResourceAsStream(name));
    }

    @SneakyThrows
    private static String read(InputStream inputStream) {
        try (BufferedInputStream bis = new BufferedInputStream(inputStream);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = bis.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            return baos.toString(StandardCharsets.UTF_8.name());
        }
    }

    private static String prependLineNumbers(String script) {
        String[] lines = script.split("\\R");
        String formatString = String.format("%%%dd: %%s%%n", String.valueOf(lines.length).length());
        return IntStream.range(0, lines.length)
                .mapToObj(i -> String.format(formatString, i + 1, lines[i].replaceAll("\\s$", "")))
                .collect(Collectors.joining());
    }

    @Getter
    @EqualsAndHashCode
    private static class CacheKey {
        private final String url;
        private final String host;

        private CacheKey(String url, String host) {
            this.url = url;
            this.host = host;
        }
    }

    private static class NonCachingLoadingCache<K, V> extends AbstractLoadingCache<K, V> {
        private final Function<K, V> function;

        private NonCachingLoadingCache(Function<K, V> function) {
            this.function = function;
        }

        @Override
        public V get(@NonNull K key) {
            return function.apply(key);
        }

        @SuppressWarnings("unchecked")
        @Override
        public V getIfPresent(@NonNull Object key) {
            return function.apply((K) key);
        }
    }
}
