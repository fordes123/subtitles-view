package org.fordes.subview.utils;

import lombok.extern.slf4j.Slf4j;
import org.fordes.subview.entity.DTO.SystemConfigDTO;
import org.fordes.subview.enums.SystemTypeEnum;
import org.fordes.subview.utils.common.ApplicationCommon;
import org.springframework.lang.NonNull;

import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * 系统相关封装
 * 
 * @author fordes
 * 
 */
@Slf4j
public final class SystemUtil {

	public static void pushTrayNotice(@NonNull String caption, @NonNull String text) {
		ApplicationCommon.getInstance().getTrayIcon().displayMessage(caption, text, TrayIcon.MessageType.NONE);
	}


	public static SystemConfigDTO getSysConfig() throws UnknownHostException {
		InetAddress addr = InetAddress.getLocalHost();
		Properties props = System.getProperties();
		return new SystemConfigDTO()
				.setIp(addr.getHostAddress())
				.setUser(props.getProperty("user.name"))
				.setArch(props.getProperty("os.arch"))
				.setHome(props.getProperty("user.home"))
				.setHost(addr.getHostName())
				.setJava(props.getProperty("java.version"))
				.setTmpdir(props.getProperty("java.io.tmpdir"))
				.setVersion(props.getProperty("os.version"))
				.setName(SystemTypeEnum.getSysType(props.getProperty("os.name")))
				.setWork(props.getProperty("user.dir"));
	}
}