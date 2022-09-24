
// https://developer.mozilla.org/en-US/docs/Web/HTTP/Proxy_servers_and_tunneling/Proxy_Auto-Configuration_PAC_file

function FindProxyForURL(url, host) {
    if (
        isPlainHostName(host) ||
        dnsDomainIs(host, ".mydomain.com") ||
        isResolvable(host)
    ) {
        return "DIRECT";
    }
    return "PROXY proxy.mydomain.com:8080";
}
