package org.fordes.subview.utils;

import cn.hutool.system.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subview.entity.DTO.SystemConfigDTO;
import org.fordes.subview.enums.SystemTypeEnum;
import org.fordes.subview.utils.common.ApplicationCommon;
import org.springframework.lang.NonNull;

import java.awt.*;

/**
 * 系统相关封装
 * 
 * @author fordes
 * 
 */
@Slf4j
public final class SystemUtils extends SystemUtil {

	public static void pushTrayNotice(@NonNull String caption, @NonNull String text) {
		ApplicationCommon.getInstance().getTrayIcon().displayMessage(caption, text, TrayIcon.MessageType.NONE);
	}


	public static SystemConfigDTO getSysConfig() {
		return new SystemConfigDTO()
				.setIp(getHostInfo().getAddress())
				.setUser(getUserInfo().getName())
				.setArch(getOsInfo().getArch())
				.setHome(getUserInfo().getHomeDir())
				.setHost(getHostInfo().getName())
				.setJava(getJavaInfo().getVersion())
				.setTmpdir(getUserInfo().getTempDir())
				.setVersion(getOsInfo().getVersion())
				.setName(SystemTypeEnum.getSysType(getOsInfo().getName()))
				.setWork(getUserInfo().getCurrentDir());
	}


}