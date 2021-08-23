package org.fordes.subview.service.data.Impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.fordes.subview.dao.FileInfoDao;
import org.fordes.subview.service.data.FileInfoService;

/**
 * @author fordes
 */
@Service
public class FileInfoServiceImpl implements FileInfoService {

    @Autowired
    private FileInfoDao fileInfoDao;



}
