COMPILE_DEPS = CORE_DEPS + JACKSON

osgi_jar_with_tests(
    karaf_command_packages = ["org.onosproject.dhcpipsat"],
    deps = COMPILE_DEPS,
)

BUNDLES = [
    "//apps/dhcpipsat-app:onos-apps-dhcpipsat-app",
]

onos_app(
    category = "Monitoring",
    description = "CAP attack POC",
    included_bundles = BUNDLES,
    title = "Vulnerable DHCP Server",
    url = "https://www.edoardoottavianelli.it/",
)
