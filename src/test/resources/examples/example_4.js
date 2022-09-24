
// https://developer.mozilla.org/en-US/docs/Web/HTTP/Proxy_servers_and_tunneling/Proxy_Auto-Configuration_PAC_file

function FindProxyForURL(url, host) {
    if (
        isPlainHostName(host) ||
        dnsDomainIs(host, ".mydomain.com") ||
        isInNet(host, "198.95.0.0", "255.255.0.0")
    ) {
        return "DIRECT";
    } else {
        return "PROXY proxy.mydomain.com:8080";
    }
}
