
// https://developer.mozilla.org/en-US/docs/Web/HTTP/Proxy_servers_and_tunneling/Proxy_Auto-Configuration_PAC_file

function FindProxyForURL(url, host) {
    if (
        (isPlainHostName(host) || dnsDomainIs(host, ".mozilla.org")) &&
        !localHostOrDomainIs(host, "www.mozilla.org") &&
        !localHostOrDomainIs(host, "merchant.mozilla.org")
    ) {
        return "DIRECT";
    } else {
        return "PROXY w3proxy.mozilla.org:8080; DIRECT";
    }
}
