
// https://developer.mozilla.org/en-US/docs/Web/HTTP/Proxy_servers_and_tunneling/Proxy_Auto-Configuration_PAC_file

function FindProxyForURL(url, host) {
    if (isPlainHostName(host) || dnsDomainIs(host, ".mydomain.com")) {
        return "DIRECT";
    } else if (shExpMatch(host, "*.com")) {
        return "PROXY proxy1.mydomain.com:8080; PROXY proxy4.mydomain.com:8080";
    } else if (shExpMatch(host, "*.edu")) {
        return "PROXY proxy2.mydomain.com:8080; PROXY proxy4.mydomain.com:8080";
    } else {
        return "PROXY proxy3.mydomain.com:8080; PROXY proxy4.mydomain.com:8080";
    }
}
