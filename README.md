This library contains a class called `ProxyAutoConfiguration` designed to manage and apply [Proxy Auto-Configuration](https://developer.mozilla.org/en-US/docs/Web/HTTP/Proxy_servers_and_tunneling/Proxy_Auto-Configuration_PAC_file) (PAC) files. The main functionalities include:

1. **Dynamic Proxy Resolution**: The class dynamically determines the appropriate proxy settings for web requests based on the contents of a PAC file specified by a URL.
2. **Caching Mechanism**: It implements a caching mechanism to optimize the performance of proxy resolution, reducing the need to repeatedly evaluate the PAC script for the same URLs.
3. **DNS Resolution Integration**: Integrates DNS resolution into the PAC script execution, allowing the script to perform DNS lookups using Java methods, which is particularly useful for environments where JavaScript's DNS resolution capabilities are limited or non-existent.
4. **Utility Methods**: Provides utility methods to read the PAC file from various sources (local or network) and tools for script evaluation and error handling within a Nashorn script engine context.

This setup is aimed at environments where proxy configurations need frequent updates or need to be applied programmatically based on complex logic included in PAC files.

There is also a utility class, `NetworkAddresses`, that is used to manage DNS cache settings on a system. The class allows the disabling of DNS caching and the setting of cache duration for both positive and negative results by modifying system properties. Specifically, the methods `disableCaching`, `setCacheTtl`, and `setNegativeCacheTtl` manipulate properties related to the TTL (time-to-live) values for DNS lookups, as described in the [Java networking properties](https://docs.oracle.com/javase/8/docs/technotes/guides/net/properties.html) documentation. These methods are useful for controlling how long DNS lookups are stored to potentially speed up network operations or ensure more frequent updates.

Unfortunately, at present this library is still limited to Java 8, since it is being used in a backwards environment that is still running that dirt old version of Java.

_Most of this README was generated by ChatGPT by asking it to create a README using the two classes. So any errors are its fault..._ 😏
