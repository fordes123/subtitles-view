package org.fordes.subtitles.view.utils.submerge.parser;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import org.fordes.subtitles.view.utils.submerge.parser.exception.InvalidFileException;
import org.fordes.subtitles.view.utils.submerge.parser.exception.InvalidSubException;
import org.fordes.subtitles.view.utils.submerge.subtitle.common.TimedTextFile;
import org.fordes.subtitles.view.utils.submerge.utils.EncodeUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.Charset;


public abstract class BaseParser<T extends TimedTextFile> implements SubtitleParser {

    /**
     * UTF-8 BOM Marker
     */
    private static final char BOM_MARKER = '\ufeff';

    @Override
    public T parse(File file) {
        try {
            return parse(file, EncodeUtils.guessEncoding(file));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T parse(File file, String charset) {

        if (!file.isFile()) {
            throw new InvalidFileException("File " + file.getName() + " is invalid");
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            return parse(fis, file.getName(), charset);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public T parse(InputStream is, String fileName) {
        try {
            return parse(is, fileName, EncodeUtils.guessEncoding(is));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T parse(InputStream is, String fileName, String charset) {
        try {
            Type type = TypeUtil.getTypeArgument(this.getClass().getGenericSuperclass());
            T sub = ReflectUtil.newInstance((Class<T>) type);
            sub.setFileName(fileName);

            try (BufferedReader br = IoUtil.getReader(is, Charset.forName(charset))) {
                skipBom(br);
                parse(br, sub);
            }
            return sub;
        } catch (IOException e) {
            throw new InvalidFileException(e);
        }
    }

    @Override
    public T parse(String str, String fileName) {
        try {
            Type type = TypeUtil.getTypeArgument(this.getClass().getGenericSuperclass());
            T sub = ReflectUtil.newInstance((Class<T>) type);
            sub.setFileName(fileName);

            try (BufferedReader br = IoUtil.getReader(StrUtil.getReader(str))) {
                skipBom(br);
                parse(br, sub);
            }
            return sub;
        } catch (IOException e) {
            throw new InvalidFileException(e);
        }
    }

    /**
     * Parse the subtitle file into a <code>ParsableSubtitle</code> object
     *
     * @param br: the buffered reader
     * @param sub : the subtitle object to fill
     * @throws IOException
     * @throws InvalidSubException if an error has occured when parsing the subtitle file
     */
    protected abstract void parse(BufferedReader br, T sub) throws IOException;

    /**
     * Ignore blank spaces and return the first text line
     *
     * @param br: the buffered reader
     * @throws IOException
     */
    protected static String readFirstTextLine(BufferedReader br) throws IOException {

        String line = null;
        while ((line = br.readLine()) != null) {
            if (!StrUtil.isEmpty(line.trim())) {
                break;
            }
        }
        return line;
    }

    /**
     * Remove the byte order mark if exists
     *
     * @param br: the buffered reader
     * @throws IOException
     */
    private static void skipBom(BufferedReader br) throws IOException {

        br.mark(4);
        if (BOM_MARKER != br.read()) {
            br.reset();
        }
    }

    /**
     * Reset the reader at the previous mark if the current line is a new section
     *
     * @param br:   the reader
     * @param line: the current line
     * @throws IOException
     */
    protected static void reset(BufferedReader br, String line) throws IOException {
        if (StrUtil.startWith(line, StrUtil.C_BRACKET_START)) {
            br.reset();
        }
    }

    /**
     * Mark the position in the reader and read the next text line
     *
     * @param br: the buffered reader
     * @return the next text line
     * @throws IOException
     */
    protected static String markAndRead(BufferedReader br) throws IOException {

        br.mark(32);
        return readFirstTextLine(br);
    }

}
