package com.uit.fooddelivery_api.modules.user.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadAvatar(MultipartFile file) throws IOException {
        // Lệnh này đẩy file byte lên mây
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        // Sau khi mây lưu xong, nó sẽ trả về một cái URL
        return uploadResult.get("url").toString();
    }
}