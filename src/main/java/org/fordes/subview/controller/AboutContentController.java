package org.fordes.subview.controller;

import cn.hutool.core.swing.DesktopUtil;
import cn.hutool.core.swing.clipboard.ClipboardUtil;
import cn.hutool.core.util.StrUtil;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.fordes.subview.enums.ToastEnum;
import org.fordes.subview.utils.constants.CommonConstants;

import javax.annotation.Resource;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author fordes on 2021/5/10
 */
@FXMLController
public class AboutContentController extends BasicController implements Initializable{

    @FXML
    private TextArea termsArea;

    @FXML
    private Label title;

    @FXML
    private GridPane root, developer, terms;

    @FXML
    private ScrollPane license;

    @Resource
    private ToastController toastController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String useTerms = "这些许可条款是您与 SubtitlesView 开发者（或其关联组织个人）之间达成的协议，条款适用于 Subtitles View 软件和软件更新（以下简称“软件”）。如果您遵守这些许可条款，您将拥有以下权利。下载或使用软件，即表示您接受这些条款。如果您不接受这些条款，则无权并且不得下载或使用软件。  \n\n" +
                "1.    安装和使用权利。您可以在任何平台的任何设备上安装和使用软件（不保证可用性）的副本。\n\n" +
                "2.    其他服务。软件可能包含提供对其他服务、网站、链接、内容、材料、集成或应用程序（包括由独立第三方提供的其他服务、网站、链接、内容、材料、集成或应用程序，统称“其他服务”）接入点或依赖其他服务的功能。您对其他服务或依赖其他服务的软件功能的使用可能受单独条款的约束，并受单独隐私政策的约束。您可以通过其他服务的网站或设置（如适用）查看这些单独条款和政策。其他服务可能未在所有地区推出。  \n\n" +
                "3.    反馈。向 开发者 提供任何有关软件的评论、建议或其他反馈（以下简称“提交内容”），即表示您授予 开发者 及其合作伙伴出于任何目的、以任何方式使用提交内容的权利。若根据某项许可的规定，开发者 由于在其软件或文档中包含了您的反馈而不得不向第三方授予软件或文档的许可，请不要向 开发者 提供这样的提交内容。这些权利在本协议终止后继续有效。  \n\n" +
                "4.    数据收集。软件可能记录有关您对软件的使用的信息，数据仅存在于本地终端，软件并不会主动通过任何形式对其进行传输。接受本协议并使用软件，即表示您同意这种数据收集方式。  \n\n" +
                "5.    许可范围。软件只授予使用许可，不是出售。本协议只授予您使用软件的某些权利。开发者 保留所有其他权利。除非适用的法律赋予您此项限制之外的权利，否则您不得（且无权）： \n\n" +
                "\t(a)  绕过软件中的任何技术限制，这些限制只允许您以特定的方式使用软件； \n\n" +
                "\t(b)  对软件进行反向工程、反向编译、反汇编、解密或试图获取软件的源代码，除非且仅限于上述限制存在以下情形：  \n\n" +
                "\t\t(i) 适用法律允许；  \n\n" +
                "\t\t(ii) 约束软件可能包含的开源组件的使用的第三方许可条款要求；或者  \n\n" +
                "\t\t(iii) 在软件所包含或链接的 GNU 宽通用公共许可协议的许可下，进行必要的库变更调试。 \n\n" +
                "\t(c)  当使用基于 Internet 的功能时，您不得以任何妨碍他人使用这些功能的方式使用它们，或试图在未经授权的情况下访问或使用任何服务、数据、帐户或网络； \n\n" +
                "\t(d)  以违反法律或创建或传播恶意软件的任何方式使用软件；或者 \n\n" +
                "\t(e)  共享、发布、分发或出借软件，或将其作为独立托管解决方案提供给他人使用，或将软件或本协议转让给任何第三方。  \n\n" +
                "6.     文档。如果文档随软件提供，则您可以复制和使用该文档，但仅供个人参考之用。 \n\n" +
                "7.     支持服务。根据本协议，开发者 没有为软件提供任何支持服务的义务。提供的任何支持均“按现状”提供，“可能存在各种缺陷”，且不包含任何形式的保证。  \n\n" +
                "8.     更新。软件不会主动检查更新，所以您必须手动下载和安装更新。您只能从 开发者 或授权来源处获得软件更新。更新信息可能不涵盖或支持所有的现有软件功能、服务或外围设备。  \n\n" +
                "9.     完整协议。本协议，以及 开发者 可能针对补充、更新或第三方应用程序提供的任何其他条款，共同构成了软件的完整协议。";
        termsArea.setText(useTerms);
    }

    public void onDeveloper() {
        root.setVisible(true);
        this.focus(developer);
        title.setText("开发者说");
    }

    public void onPrivacy() {
        root.setVisible(true);
        this.focus(terms);
        title.setText("隐私声明");
    }

    public void onLicense() {
        root.setVisible(true);
        this.focus(license);
        title.setText("开源许可");
    }

    public void onClip(MouseEvent mouseEvent) {
        ClipboardUtil.setStr(((Label)mouseEvent.getSource()).getText());
        toastController.pushMessage(ToastEnum.SUCCESS, "已复制到剪贴板");
    }


    public void onClick(MouseEvent mouseEvent) {
        DesktopUtil.browse(StrUtil.format(CommonConstants.GITHUB_LINK_FORMAT, ((Label)mouseEvent.getSource()).getText()));
    }
}
