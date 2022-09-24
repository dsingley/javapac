package com.dsingley.proxyautoconfiguration;

import org.junit.jupiter.api.Test;

import static com.dsingley.proxyautoconfiguration.ProxyAutoConfiguration.read;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ProxyAutoConfigurationTest {

    @Test
    void testExample1() {
        ProxyAutoConfiguration pac = new ProxyAutoConfiguration(read("/examples/example_1.js"));
        assertEquals("DIRECT", pac.findProxyForURL("https://nodots"));
        assertEquals("DIRECT", pac.findProxyForURL("https://anything.mozilla.org"));
        assertEquals("PROXY w3proxy.mozilla.org:8080; DIRECT", pac.findProxyForURL("https://somewhere.else.com"));
    }

    @Test
    void testExample2() {
        ProxyAutoConfiguration pac = new ProxyAutoConfiguration(read("/examples/example_2.js"));
        assertEquals("DIRECT", pac.findProxyForURL("https://nodots"));
        assertEquals("DIRECT", pac.findProxyForURL("https://mostthings.mozilla.org"));
        assertEquals("PROXY w3proxy.mozilla.org:8080; DIRECT", pac.findProxyForURL("https://www.mozilla.org"));
        assertEquals("PROXY w3proxy.mozilla.org:8080; DIRECT", pac.findProxyForURL("https://merchant.mozilla.org"));
        assertEquals("PROXY w3proxy.mozilla.org:8080; DIRECT", pac.findProxyForURL("https://somewhere.else.com"));
    }

    @Test
    void testExample3() {
        ProxyAutoConfiguration pac = new ProxyAutoConfiguration(read("/examples/example_3.js"));
        assertEquals("DIRECT", pac.findProxyForURL("https://nodots"));
        assertEquals("DIRECT", pac.findProxyForURL("https://anything.mydomain.com"));
        assertEquals("DIRECT", pac.findProxyForURL("https://google.com"));
        assertEquals("PROXY proxy.mydomain.com:8080", pac.findProxyForURL("https://unresolvable.hostname"));
    }

    @Test
    void testExample4() {
        ProxyAutoConfiguration pac = new ProxyAutoConfiguration(read("/examples/example_4.js"));
        assertEquals("DIRECT", pac.findProxyForURL("https://nodots"));
        assertEquals("DIRECT", pac.findProxyForURL("https://anything.mydomain.com"));
        assertEquals("DIRECT", pac.findProxyForURL("https://198.95.1.2"));
        assertEquals("PROXY proxy.mydomain.com:8080", pac.findProxyForURL("https://google.com"));
    }

    @Test
    void testExample5() {
        ProxyAutoConfiguration pac = new ProxyAutoConfiguration(read("/examples/example_5.js"));
        assertEquals("DIRECT", pac.findProxyForURL("https://nodots"));
        assertEquals("DIRECT", pac.findProxyForURL("https://anything.mydomain.com"));
        assertEquals("PROXY proxy1.mydomain.com:8080; PROXY proxy4.mydomain.com:8080", pac.findProxyForURL("https://google.com"));
        assertEquals("PROXY proxy2.mydomain.com:8080; PROXY proxy4.mydomain.com:8080", pac.findProxyForURL("https://vt.edu"));
        assertEquals("PROXY proxy3.mydomain.com:8080; PROXY proxy4.mydomain.com:8080", pac.findProxyForURL("https://mozilla.org"));
    }

    @Test
    void testExample6() {
        ProxyAutoConfiguration pac = new ProxyAutoConfiguration(read("/examples/example_6.js"));
        assertEquals("PROXY http-proxy.mydomain.com:8080", pac.findProxyForURL("http://example"));
        assertEquals("PROXY ftp-proxy.mydomain.com:8080", pac.findProxyForURL("ftp://example"));
        assertEquals("PROXY security-proxy.mydomain.com:8080", pac.findProxyForURL("https://example"));
    }
}