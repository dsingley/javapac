
// https://developer.mozilla.org/en-US/docs/Web/HTTP/Proxy_servers_and_tunneling/Proxy_Auto-Configuration_PAC_file

function FindProxyForURL(url, host) {
    if (url.startsWith("http:")) {
        return "PROXY http-proxy.mydomain.com:8080";
    } else if (url.startsWith("ftp:")) {
        return "PROXY ftp-proxy.mydomain.com:8080";
    } else if (url.startsWith("gopher:")) {
        return "PROXY gopher-proxy.mydomain.com:8080";
    } else if (url.startsWith("https:") || url.startsWith("snews:")) {
        return "PROXY security-proxy.mydomain.com:8080";
    }
    return "DIRECT";
}
