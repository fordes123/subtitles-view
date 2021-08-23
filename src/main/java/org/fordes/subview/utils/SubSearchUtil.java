package org.fordes.subview.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subview.entity.DTO.search.SearchItem;
import org.fordes.subview.entity.DTO.search.SearchResult;
import org.fordes.subview.enums.SubtitlesSearchEnum;
import org.fordes.subview.utils.constants.CommonConstants;
import org.fordes.subview.utils.constants.SearchConstants;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author fordes on 2021/7/21
 */
@Slf4j
public class SubSearchUtil {

    public static SearchResult download(SubtitlesSearchEnum engine, Object request, String fileName) {
        SearchResult result = new SearchResult();
        String path = StrUtil.EMPTY;
        TimeInterval interval = DateUtil.timer();
        try {
            switch (engine) {
                case ASSRT:
                    path = download_ASSRT(request, fileName);
                    break;
                case ZMK:
                    path = download_ZMK(request);
                    break;
                case DDZM:
                    path = download_DDZM(request, fileName);
                    break;
            }
        } catch (Exception e) {
            log.error("字幕文件下载失败，详情：{}", e.getMessage());
            // TODO: 2021/1/5 删除已下载的文件和路径
        } finally {
            log.debug("{} 字幕下载耗时 => {} ms", engine.toString(), interval.interval());
        }

        List<File> files = Lists.newArrayList();
        if (StrUtil.isNotEmpty(path)) {
            if (FileUtil.isFile(path)) {
                files.add(FileUtil.newFile(path));
            }else files.addAll(Arrays.stream(FileUtil.ls(path)).collect(Collectors.toList()));
        }
        List<SearchItem> data = files.stream().map(item
                -> new SearchItem().setTitle(item.getName()).setFile(item)).collect(Collectors.toList());
        return result.setData(data).setFile(true);
    }

    public static SearchResult search(SubtitlesSearchEnum engine, String srt, Object target) {
        try {
            switch (engine) {
                case ASSRT:
                    return search_ASSRT(srt, target);
                case ZMK:
                    return search_ZMK(srt, target);
                case DDZM:
                    return search_DDZM(srt, target);
            }
        } catch (Exception e) {
            log.error("搜索=> {} 出错 => {}", engine.toString(), e.getMessage());
        }
        return new SearchResult();
    }

    public static SearchResult search(SubtitlesSearchEnum engine, String srt) {
        return search(engine, srt, null);
    }


    private static SearchResult search_ASSRT(String srt, Object target) throws IOException {
        SearchResult result = new SearchResult();
        List<SearchItem> rows = Lists.newArrayList();
        Document document = ObjectUtil.isNull(target) ?
                Jsoup.connect(StrUtil.format(SearchConstants.ASSRT_SEARCH_FORMAT, srt)).get() :
                Jsoup.connect((String) target).get();
        Elements elements = document.getElementsByClass("subitem");
        for (Element element : elements) {
            Element secondaryEle = element.getElementById("meta_top");
            if (secondaryEle != null) {
                Element titleEle = element.getElementsByClass("introtitle").first();
                Elements sublist_div = element.getElementById("sublist_div").getElementsByTag("span");

                Matcher matcher = Pattern.compile("javascript:location.href='(/download/\\d+/(.*\\.\\w+))'").matcher(element.getElementById("downsubbtn").attr("onclick"));
                if (matcher.find()) {
                    rows.add(new SearchItem()
                            .setTitle(titleEle.attr("title"))
                            .setSecondary(secondaryEle.text())
                            .setTags(sublist_div.stream().map(Element::text).filter(StrUtil::isNotEmpty).collect(Collectors.toSet()))
                            .setIs_down(true)
                            .setFile_name(URLUtil.decode(matcher.group(2)))
                            .setRequest(SearchConstants.ASSRT_DOWNLOAD_HOST + matcher.group(1)));
                }
            }
        }
        Element pageEls = document.getElementsByClass("pagelinkcard").first();
        if (pageEls != null) {
            int itemSite = pageEls.getElementsByTag("a").size();
            if (itemSite >= 2) {
                int pageSize = itemSite - 2;
                int current = Integer.parseInt(pageEls.getElementById("pl-current").text());
                if (current < pageSize) {
                    result.setNext(StrUtil.format(SearchConstants.ASSRT_SEARCH_PAGE_FORMAT, srt, current + 1));
                }
            }
        }
        return result.setData(rows);
    }

    private static SearchResult search_ZMK(String srt, Object target) throws IOException {
        SearchResult result = new SearchResult();
        List<SearchItem> rows = Lists.newArrayList();
        Document document = ObjectUtil.isNull(target) ?
                Jsoup.connect(StrUtil.format(SearchConstants.ZIMUKU_SEARCH_FORMAT, srt)).get() :
                ((Connection) target).get();
        Element ele = document.getElementById("subtb");
        if (ele != null) {
            //二级页面
            Elements elements = document.getElementsByTag("tbody").first().getElementsByTag("tr");
            for (Element e : elements) {
                Element title = e.getElementsByTag("a").first();
                Elements tags = e.getElementsByClass("label label-info");
                rows.add(new SearchItem().setTitle(title.attr("title"))
                        .setIs_down(true)
                        .setTags(tags.stream().map(Element::text).collect(Collectors.toSet()))
                        .setRequest(StrUtil.concat(false, SearchConstants.ZIMUKU_HOST,
                                StrUtil.replace(title.attr("href"), "detail", "dld"))));
            }
        } else {
            //一级页面
            Elements elements = document.getElementsByClass("item");
            for (Element e : elements) {
                Element imgEle = e.getElementsByClass("lazy").first();
                Elements titles = e.getElementsByClass("title").first().getElementsByTag("p");
                rows.add(new SearchItem().setTitle(titles.first().text())
                        .setSecondary(titles.last().text())
                        .setImg_url(imgEle.attr("data-original"))
                        .setIs_down(false)
                        .setRequest(Jsoup.connect(SearchConstants.ZIMUKU_HOST + titles.first().getElementsByTag("a").attr("href"))));
            }

            if (document.hasClass("next")) {
                Element page = document.getElementsByClass("next").last();
                result.setNext(Jsoup.connect(SearchConstants.SUB_HD_SEARCH_FORMAT + page.attr("href")));
            }
        }
        return result.setData(rows);
    }

    private static SearchResult search_DDZM(String srt, Object target) throws IOException {
        SearchResult result = new SearchResult();
        List<SearchItem> rows = Lists.newArrayList();
        if (ObjectUtil.isNull(target)) {
            Document document = Jsoup.connect(StrUtil.format(SearchConstants.DDZM_SEARCH_FORMAT, srt)).get();
            Elements elements = document.getElementsByClass("pl");
            elements.remove(0);
            for (Element e : elements) {
                Element title = e.getElementsByClass("pianmang").first().getElementsByTag("a").first();
                Element sec = e.getElementsByClass("aliases").first().getElementsByTag("a").first();
                rows.add(new SearchItem()
                        .setTitle(title.attr("title"))
                        .setSecondary(sec.attr("title"))
                        .setTags(Sets.newHashSet())
                        .setIs_down(false)
                        .setRequest(Jsoup.connect(StrUtil.concat(true, SearchConstants.DDZM_HOST, title.attr("href")))));
            }
        } else {
            Document document = ((Connection) target).get();
            Elements elements = document.getElementsByTag("li");
            elements.remove(0);
            for (Element e : elements) {
                Element title = e.children().first().getElementsByTag("a").first();
                Elements tags = e.children().first().getElementsByTag("span");
                Elements lang = e.getElementsByClass("lang").first().getElementsByTag("img");
                rows.add(new SearchItem()
                        .setTitle(title.attr("title"))
                        .setTags(Sets.newHashSet("格式：" + CollUtil.join(tags.stream().map(Element::text).filter(StrUtil::isNotEmpty).collect(Collectors.toSet()), StrUtil.COMMA),
                                "语言：" + CollUtil.join(lang.stream().map(Element::text).filter(StrUtil::isNotEmpty).collect(Collectors.toSet()), StrUtil.COMMA)))
                        .setIs_down(true)
                        .setFile_name(title.attr("title"))
                        .setRequest(title.attr("href")));
            }
        }


        return result.setData(rows);
    }

    private static String download_ASSRT(Object request, String fileName) {
        if (StrUtil.isEmpty(fileName)) {
            fileName = String.valueOf(System.currentTimeMillis());
        }
        byte[] data = HttpUtil.downloadBytes(request.toString());
        FileUtil.writeBytes(data, CommonConstants.TEMP_PATH + fileName);
        return ZipUtil.unZipToCurrentPath(CommonConstants.TEMP_PATH + fileName);
    }

    private static String download_ZMK(Object request) throws Exception {

        Elements downs = Jsoup.connect(request.toString()).get().getElementsByTag("li");
        for (Element d : downs) {
            String link = SearchConstants.ZIMUKU_DOWN + d.getElementsByTag("a").attr("href");
            HttpResponse resp = HttpRequest
                    .get(link)
                    .header("Host", "zmk.pw")
                    .header("Referer", request.toString())
                    .setMaxRedirectCount(3).timeout(8000).execute();
            String name = resp.header("Content-Disposition");
            if (StrUtil.isNotEmpty(name)) {
                String file = StrUtil.concat(true, CommonConstants.TEMP_PATH,
                        StrUtil.BACKSLASH, ReUtil.get("\"(.*)\"", name, 1));
                FileUtil.writeFromStream(resp.bodyStream(), file);
                return ZipUtil.unZipToCurrentPath(file);
            }
        }
        throw new Exception();
    }

    private static String download_DDZM(Object request, String fileName) {
        HttpResponse resp = HttpRequest.get(request.toString()).execute();
        String name = resp.header(Header.CONTENT_DISPOSITION);
        if (StrUtil.isNotEmpty(name)) {
            fileName = URLUtil.decode(ReUtil.getGroup1("filename=(.*)", name));
        }
        File file = FileUtil.file(CommonConstants.TEMP_PATH+ fileName);
        FileUtil.writeBytes(resp.bodyBytes(), file);
        if (ZipUtil.formats.contains(FileUtil.getSuffix(fileName))) {
            return ZipUtil.unZipToCurrentPath(file);
        }else return file.getPath();
    }

}
