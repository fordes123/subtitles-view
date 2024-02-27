package org.fordes.subtitles.view.utils.submerge.parser;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import org.fordes.subtitles.view.utils.submerge.parser.exception.InvalidAssSubException;
import org.fordes.subtitles.view.utils.submerge.subtitle.ass.*;
import org.fordes.subtitles.view.utils.submerge.utils.ColorUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.DateTimeException;
import java.util.*;

/**
 * Parse SSA/ASS subtitles
 */
public class ASSParser extends BaseParser<ASSSub> {

    /**
     * Comments: lines that start with this character are ignored
     */
    private static final String COMMENTS_MARK = ";";

    @Override
    protected void parse(BufferedReader br, ASSSub sub) throws IOException, InvalidAssSubException {

        String line = readFirstTextLine(br);
        if (line != null && !StrUtil.equalsAnyIgnoreCase("[script info]", StrUtil.trim(line))) {
            throw new InvalidAssSubException("The line that says “[Script Info]” must be the first line in the script.");
        }

        // [Script Info]
        sub.setScriptInfo(parseScriptInfo(br));
        while ((line = readFirstTextLine(br)) != null) {
            if (line.matches("(?i:^\\[v.*styles\\+?]$)")) {
                // [V4+ Styles]
                sub.setStyle(parseStyle(br));
            } else if (line.equalsIgnoreCase("[events]")) {
                // [Events]
                sub.setEvents(parseEvents(br));
            }
        }

        if (sub.getStyle().isEmpty()) {
            throw new InvalidAssSubException("Missing style definition");
        }

        if (sub.getEvents().isEmpty()) {
            throw new InvalidAssSubException("No text line found");
        }
    }

    /**
     * Parse the events section from the reader. <br/>
     * <p>
     * Example of events section:
     *
     * <pre>
     * [Events]
     * Format: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text
     * Dialogue: 0,0:02:30.84,0:02:34.70,StlyeOne,,0000,0000,0000,,A text line
     * Dialogue: 0,0:02:34.92,0:02:37.54,StyleTwo,,0000,0000,0000,,Another text line
     * </pre>
     *
     * @param br: the buffered reader
     * @throws IOException
     * @throws InvalidAssSubException
     * @throws IOException
     */
    private static Set<Events> parseEvents(BufferedReader br) throws IOException, InvalidAssSubException {
        String[] eventsFormat = findFormat(br, "events");

        Set<Events> events = new TreeSet<>();
        String line = readFirstTextLine(br);

        while (line != null && !StrUtil.startWith(line, StrUtil.C_BRACKET_START)) {
            if (StrUtil.startWith(line, Events.DIALOGUE)) {
                String info = findInfo(line, Events.DIALOGUE);
                String[] dialogLine = StrUtil.splitToArray(info, Events.SEP);
                //StringUtils.splitByWholeSeparatorPreserveAllTokens(info, Events.SEP);

                int lengthDialog = dialogLine.length;
                int lengthFormat = eventsFormat.length;

                if (lengthDialog < lengthFormat) {
                    throw new InvalidAssSubException("Incorrect dialog line : " + info);
                }
                if (lengthDialog > lengthFormat) {
                    // The text field contains commas
                    StringJoiner joiner = new StringJoiner(Events.SEP);
                    for (int i = lengthFormat - 1; i < lengthDialog; i++) {
                        joiner.add(dialogLine[i]);
                    }
                    dialogLine[lengthFormat - 1] = joiner.toString();
                    dialogLine = Arrays.copyOfRange(dialogLine, 0, lengthFormat);
                }
                events.add(parseDialog(eventsFormat, dialogLine));
            }

            line = markAndRead(br);

        }

        reset(br, line);
        return events;
    }

    /**
     * Parse the style section from the reader. <br/>
     * <p>
     * Example of style section:
     *
     * <pre>
     * [V4+ Styles]
     * Format: Name,Fontname,Fontsize,PrimaryColour,SecondaryColour,OutlineColour
     * Style: StyleOne,Arial,16,64250,16777215,0
     * Style: StyleTwo,Arial,16,16383999,16777215,0
     * </pre>
     *
     * @param br: the buffered reader
     * @throws IOException
     * @throws InvalidAssSubException
     */
    private static List<V4Style> parseStyle(BufferedReader br) throws IOException, InvalidAssSubException {
        String[] styleFormat = findFormat(br, "styles");

        List<V4Style> styles = new ArrayList<>();
        String line = readFirstTextLine(br);
        int index = 1;
        while (line != null && !StrUtil.startWith(line, StrUtil.BRACKET_START)) {
            if (line.startsWith(V4Style.STYLE) && !line.startsWith(COMMENTS_MARK)) {
                String[] textLine = line.split(StrUtil.COLON);
                if (textLine.length > 1) {
                    String[] styleLine = textLine[1].split(V4Style.SEP);
                    styles.add(parseV4Style(styleFormat, styleLine, index));
                    index++;
                }
            }
            line = markAndRead(br);
        }

        while (line != null && !StrUtil.startWith(line, StrUtil.BRACKET_START)) {
            if (StrUtil.startWith(line, V4Style.STYLE)) {
                List<String> textLine = StrUtil.split(line, StrUtil.COLON);
                if (!textLine.isEmpty()) {
                    String[] styleLine = StrUtil.splitToArray(textLine.get(1), V4Style.SEP);
                    styles.add(parseV4Style(styleFormat, styleLine, index));
                    index++;
                }
            }
        }

        reset(br, line);
        return styles;
    }

    /**
     * Return the Events object from text dialog line
     *
     * @param eventsFormat: the format definition
     * @param dialogLine:   the dialog line
     * @return the Events object
     * @throws InvalidAssSubException
     */
    private static Events parseDialog(String[] eventsFormat, String[] dialogLine) throws InvalidAssSubException {

        Events events = new Events();

        for (int i = 0; i < eventsFormat.length; i++) {
            String property = StringUtils.uncapitalize(eventsFormat[i].trim());
            String value = dialogLine[i].trim();

            try {
                switch (property) {
                    case "start":
                        events.getTime().setStart(ASSTime.fromString(value));
                        break;
                    case "end":
                        events.getTime().setEnd(ASSTime.fromString(value));
                        break;
                    case "text":
                        List<String> textLines = Arrays.asList(value.split("\\\\N"));
                        events.setTextLines(new ArrayList<>(textLines));
                        break;
                    default:
                        String error = callProperty(events, property, value);
                        if (error != null) {
                            throw new InvalidAssSubException(StrUtil.format("Invalid property ({}) {}", property, value));
                        }
                        break;
                }
            } catch (DateTimeException e) {
                throw new InvalidAssSubException(StrUtil.format("Invalid property ({}) {}", property, value));
            }

        }
        return events;
    }

    /**
     * Return the V4Style object from text style line
     *
     * @param styleFormat: format line
     * @param styleLine:   the style line
     * @param lineIndex:   the line index
     * @return the style object
     * @throws InvalidAssSubException
     */
    private static V4Style parseV4Style(String[] styleFormat, String[] styleLine, int lineIndex)
            throws InvalidAssSubException {

        String message = "Style at index " + lineIndex + ": ";

        if (styleFormat.length != styleLine.length) {
            throw new InvalidAssSubException(message + "does not match style definition");
        }

        V4Style style = new V4Style();
        for (int i = 0; i < styleFormat.length; i++) {
            String property = StringUtils.uncapitalize(styleFormat[i].trim());
            String value = styleLine[i].trim();

            if (StrUtil.containsIgnoreCase(property, "colour")) {
                try {
                    Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    int bgr = getBGR(value);
                    if (bgr != -1) {
                        value = Integer.toString(bgr);
                    }
                }
            }

            String error = callProperty(style, property, value);

            if (error != null) {
                throw new InvalidAssSubException(message + error);
            }
        }

        if (StrUtil.isEmpty(style.getName())) {
            throw new InvalidAssSubException(message + " missing name");
        }

        return style;
    }

    /**
     * Get the BGR code from the &HBBGGRR or &HAABBGGRR pattern
     *
     * @param value: the value to convert
     * @return the bgr code
     */
    private static int getBGR(String value) {

        int length = value.length();
        int bgr = -1;
        if (length == 10) {
            // From ASS
            bgr = ColorUtils.HAABBGGRRToBGR(value);
        } else if (length == 8) {
            // From SSA
            bgr = ColorUtils.HBBGGRRToBGR(value);
        }
        return bgr;
    }

    /**
     * Parse the script info section from the reader. <br/>
     * <p>
     * Example of script info section:
     *
     * <pre>
     * [Script Info]
     * ScriptType: v4.00+
     * Collisions: Normal
     * Timer: 100,0000
     * Title: My movie title
     * </pre>
     *
     * @param br: the buffered reader
     * @throws IOException
     * @throws InvalidAssSubException
     */
    private static ScriptInfo parseScriptInfo(BufferedReader br) throws IOException, InvalidAssSubException {

        ScriptInfo scriptInfo = new ScriptInfo();
        String line = readFirstTextLine(br);

        while (line != null && !StrUtil.startWith(line, StrUtil.BRACKET_START)) {

            if (!line.startsWith(COMMENTS_MARK)) {

                String[] split = line.split(ScriptInfo.SEP);
                if (split.length > 1) {
                    String property = StrUtil.lowerFirst(StrUtil.cleanBlank(split[0]));

                    StringJoiner joiner = new StringJoiner(ScriptInfo.SEP);
                    for (int i = 1; i < split.length; i++) {
                        joiner.add(split[i]);
                    }
                    String value = joiner.toString().trim();

                    String error = callProperty(scriptInfo, property, value);

                    if (error != null) {
                        throw new InvalidAssSubException("Script info : " + error);
                    }
                }
            }
            line = markAndRead(br);
        }

        reset(br, line);
        return scriptInfo;
    }

    /**
     * Call a specific property of an object with reflection
     *
     * @param object:   the object to set a property
     * @param property: the property to define
     * @param value:    the value to set
     * @return the error message if an error has occured, null otherwise
     */
    private static String callProperty(Object object, String property, String value) {

        String error = null;


        PropertyDescriptor descriptor = BeanUtil.getPropertyDescriptor(object.getClass(), property);

        if (descriptor != null) {
            String type = descriptor.getPropertyType().getSimpleName();
            switch (type) {
                case "String":
                    BeanUtil.setProperty(object, property, value);
                    break;
                case "int":
                    BeanUtil.setProperty(object, property, Convert.toInt(value));
                    break;
                case "boolean":
                    BeanUtil.setProperty(object, property, Convert.toBool(value));
                    break;
                case "double":
                    BeanUtil.setProperty(object, property, Convert.toDouble(StrUtil.replace(value, StrUtil.COMMA, StrUtil.DOT)));
                    break;
                default:
                    break;
            }
        }

        return error;
    }

    /**
     * Get the format string definition
     *
     * @param br:          the buffered reader
     * @param sectionName: the name of the section to parse
     * @return the format string definition
     * @throws IOException
     * @throws InvalidAssSubException
     */
    private static String[] findFormat(BufferedReader br, String sectionName) throws IOException,
            InvalidAssSubException {

        String line = readFirstTextLine(br);
        if (StrUtil.isEmpty(line)) {
            throw new InvalidAssSubException("Missing format definition in " + sectionName + " section");
        }
        if (!StrUtil.startWith(line.trim(), ASSSub.FORMAT)) {
            throw new InvalidAssSubException(StrUtil.upperFirst(sectionName) + " definition must start with 'Format' line");
        }
        return StrUtil.splitToArray(findInfo(line, ASSSub.FORMAT), V4Style.SEP);
    }

    /**
     * Find the information after ":" in a text line
     *
     * @param line:   the line
     * @param search: the information to search
     * @return info or null if the info is empty / not found
     */
    private static String findInfo(String line, String search) {
        if (StrUtil.startWithIgnoreCase(line.trim(), search) && line.indexOf(StrUtil.COLON) > 0) {
            return line.substring(line.indexOf(StrUtil.COLON) + 1).trim();
        }
        return null;
    }

}
