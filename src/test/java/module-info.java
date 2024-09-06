module hacklib.tests {
    requires org.junit.jupiter.api;
    requires com.shionic.hacklib;
    requires jdk.unsupported;
    opens com.github.shionic.hacklib.tests to org.junit.platform.commons;
}