module fontbox {
    requires pdfbox.io;
    requires pdfbox.platform;

    exports org.apache.fontbox;
    exports org.apache.fontbox.afm;
    exports org.apache.fontbox.cff;
    exports org.apache.fontbox.cmap;
    exports org.apache.fontbox.encoding;
    exports org.apache.fontbox.pfb;
    exports org.apache.fontbox.ttf;
    exports org.apache.fontbox.ttf.gsub;
    exports org.apache.fontbox.ttf.model;
    exports org.apache.fontbox.ttf.table.common;
    exports org.apache.fontbox.ttf.table.gsub;
    exports org.apache.fontbox.type1;
    exports org.apache.fontbox.util;
    exports org.apache.fontbox.util.autodetect;
}