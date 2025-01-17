module pdfbox {
    requires fontbox;
    requires pdfbox.io;
    requires pdfbox.platform;
    requires org.bouncycastle.provider;
    requires org.bouncycastle.util;
    requires org.bouncycastle.pkix;

    exports org.apache.pdfbox;
    exports org.apache.pdfbox.contentstream;
    exports org.apache.pdfbox.contentstream.operator;
    exports org.apache.pdfbox.contentstream.operator.state;
    exports org.apache.pdfbox.cos;
    exports org.apache.pdfbox.filter;
    exports org.apache.pdfbox.multipdf;
    exports org.apache.pdfbox.pdfparser;
    exports org.apache.pdfbox.pdfparser.xref;
    exports org.apache.pdfbox.pdfwriter;
    exports org.apache.pdfbox.pdfwriter.compress;
    exports org.apache.pdfbox.pdmodel;
    exports org.apache.pdfbox.pdmodel.common;
    exports org.apache.pdfbox.pdmodel.common.filespecification;
    exports org.apache.pdfbox.pdmodel.common.function;
    exports org.apache.pdfbox.pdmodel.common.function.type4;
    exports org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure;
    exports org.apache.pdfbox.pdmodel.documentinterchange.markedcontent;
    exports org.apache.pdfbox.pdmodel.documentinterchange.prepress;
    exports org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf;
    exports org.apache.pdfbox.pdmodel.encryption;
    exports org.apache.pdfbox.pdmodel.fixup;
    exports org.apache.pdfbox.pdmodel.fixup.processor;
    exports org.apache.pdfbox.pdmodel.font;
    exports org.apache.pdfbox.pdmodel.font.encoding;
    exports org.apache.pdfbox.pdmodel.graphics;
    exports org.apache.pdfbox.pdmodel.graphics.blend;
    exports org.apache.pdfbox.pdmodel.graphics.color;
    exports org.apache.pdfbox.pdmodel.graphics.form;
    exports org.apache.pdfbox.pdmodel.graphics.image;
    exports org.apache.pdfbox.pdmodel.graphics.optionalcontent;
    exports org.apache.pdfbox.pdmodel.graphics.state;
    exports org.apache.pdfbox.pdmodel.interactive.action;
    exports org.apache.pdfbox.pdmodel.interactive.annotation;
    exports org.apache.pdfbox.pdmodel.interactive.annotation.layout;
    exports org.apache.pdfbox.pdmodel.interactive.digitalsignature;
    exports org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible;
    exports org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination;
    exports org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline;
    exports org.apache.pdfbox.pdmodel.interactive.form;
    exports org.apache.pdfbox.pdmodel.interactive.measurement;
    exports org.apache.pdfbox.pdmodel.interactive.pagenavigation;
    exports org.apache.pdfbox.pdmodel.interactive.viewerpreferences;
    exports org.apache.pdfbox.printing;
    exports org.apache.pdfbox.rendering;
    exports org.apache.pdfbox.text;
    exports org.apache.pdfbox.util;
    exports org.apache.pdfbox.util.filetypedetector;
}