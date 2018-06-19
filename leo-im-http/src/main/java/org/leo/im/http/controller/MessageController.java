package org.leo.im.http.controller;

import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.leo.im.api.dto.FileDTO;
import org.leo.im.api.dto.MessageDTO;
import org.leo.im.api.provider.ServiceFactory;
import org.leo.im.api.service.MessageService;
import org.leo.im.http.file.FileStorage;
import org.leo.im.http.file.FileStorageFactory;
import org.leo.im.http.vo.MessageVO;
import org.leo.im.service.support.ServiceProxy;
import org.leo.im.util.BeanUtils;
import org.leo.web.annotation.DeleteMapping;
import org.leo.web.annotation.GetMapping;
import org.leo.web.annotation.PostMapping;
import org.leo.web.annotation.RequestBody;
import org.leo.web.annotation.RequestHeader;
import org.leo.web.annotation.RequestMapping;
import org.leo.web.annotation.RequestParam;
import org.leo.web.annotation.RestController;
import org.leo.web.annotation.UploadFile;
import org.leo.web.annotation.UrlEncodedForm;
import org.leo.web.multipart.MultipartFile;
import org.leo.web.rest.ResponseEntity;

import com.alibaba.fastjson.JSONObject;

import io.netty.handler.codec.http.FullHttpRequest;

/**
 * 消息控制器
 * 
 * @author Leo
 * @date 2018/5/16
 */
@RestController()
@RequestMapping("/messages")
public final class MessageController extends BaseController {

    @GetMapping("")
    public ResponseEntity<List<MessageVO>> listMessage(@RequestParam("channelId") String channelId,
            @RequestParam("maxCreateAt") long maxCreateAt, @RequestParam("limit") int limit) {
        MessageService serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createMessageService());
        List<MessageDTO> dtoList = serviceProxy.listMessage(channelId, maxCreateAt, limit);
        List<MessageVO> voList = new ArrayList<>(dtoList.size());
        for(MessageDTO dto : dtoList) {
            MessageVO vo = new MessageVO();
            BeanUtils.copyProperties(dto, vo);
            voList.add(vo);
        }
        return ResponseEntity.ok(voList);
    }
    
    @PostMapping("")
    public ResponseEntity<?> saveMessage(FullHttpRequest request, @RequestHeader("X-Token") String token,
            @RequestBody String body) {
        String userId = this.getSubjectFromJwt(token, "userId");
        JSONObject json = JSONObject.parseObject(body);
        MessageDTO dto = new MessageDTO();
        dto.setSenderId(userId);
        if(json.containsKey("type")) {
            dto.setType(json.getString("type"));
        }
        dto.setChannelId(json.getString("channelId"));
        dto.setContent(json.getString("content"));
        dto.setCreateAt(new Date().getTime());
        MessageService serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createMessageService());
        MessageDTO returnDTO = serviceProxy.saveMessage(dto);
        if(returnDTO != null) {
            MessageVO vo = new MessageVO();
            BeanUtils.copyProperties(returnDTO, vo);
            return ResponseEntity.created(vo);
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/read")
    public ResponseEntity<?> readMessage(@RequestHeader("X-Token") String token, @RequestBody String body) {
        String userId = this.getSubjectFromJwt(token, "userId");
        JSONObject json = JSONObject.parseObject(body);
        MessageService serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createMessageService());
        int count = serviceProxy.readMessage(json.getString("channelId"), userId, json.getShortValue("total"));
        return ResponseEntity.created(count);
    }
    
    @DeleteMapping("")
    public ResponseEntity<?> removeMessage(@RequestHeader("X-Token") String token, @RequestBody String body) {
        String userId = this.getSubjectFromJwt(token, "userId");
        MessageService serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createMessageService());
        JSONObject json = JSONObject.parseObject(body);
        int count = serviceProxy.removeMessage(json.getLongValue("messageId"), userId, json.getString("channelId"),
                json.getString("toUserId"));
        if(count == 1) {
            return ResponseEntity.noContent(count);
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/files")
    public ResponseEntity<?> uploadFile(@UploadFile MultipartFile file, @RequestHeader("X-Token") String token,
            @UrlEncodedForm Map<String, String> form) {
        String userId = this.getSubjectFromJwt(token, "userId");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String fileKey = sdf.format(new Date()) + "/" + UUID.randomUUID().toString().replace("-", "");
        
        // 保存文件
        FileStorage fs = FileStorageFactory.newInstance();
        fs.save(fileKey, file.getFileName(), file.getFileData());
        
        int width = Integer.parseInt(form.get("imageWidth"));
        int height = Integer.parseInt(form.get("imageHeight"));
        short thumbWidth = getThumbWidth(width);
        short thumbHeight = getThumbHeight(height);
        short[] realThumbSize = new short[] { 0, 0 };
        // 如果是图片，保存略缩图
        if(this.isImage(file.getFileType())) {
            realThumbSize = fs.saveThumb(fileKey + "/thumb", file.getFileName(), file.getFileData(), thumbWidth, thumbHeight);
        }
        
        // 保存文件信息到数据库
        MessageService serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createMessageService());
        FileDTO fileDTO = new FileDTO();
        fileDTO.setName(file.getFileName());
        fileDTO.setExtension(getFileExtension(file.getFileName()));
        fileDTO.setSize(Integer.parseInt(form.get("size")));
        fileDTO.setMimeType(file.getFileType());
        fileDTO.setWidth(width);
        fileDTO.setHeight(height);
        fileDTO.setThumbWidth(realThumbSize[0]);
        fileDTO.setThumbHeight(realThumbSize[1]);
        fileDTO.setPath(fileKey);
        String fileId = serviceProxy.saveFile(fileDTO);
        
        // 保存消息
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setSenderId(userId);
        messageDTO.setChannelId(form.get("channelId"));
        messageDTO.setCreateAt(new Date().getTime());
        messageDTO.setFileId(fileId);
        MessageDTO returnDTO = serviceProxy.saveMessage(messageDTO);
        if(returnDTO != null) {
            MessageVO vo = new MessageVO();
            BeanUtils.copyProperties(returnDTO, vo);
            return ResponseEntity.created(vo);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/files")
    public ResponseEntity<RandomAccessFile> getFile(@RequestParam("fileName") String fileName, @RequestParam("fullPath") String fullPath, 
            @RequestParam("mimetype") String mimetype) {
        FileStorage fs = FileStorageFactory.newInstance();
        RandomAccessFile raf = fs.read(fullPath);
        return ResponseEntity.ok(raf, mimetype, fileName);
    }
    
    /**
     * 判断是否为图片类型
     * @param fileType
     * @return
     */
    private boolean isImage(String fileType) {
        if("image/jpeg".equalsIgnoreCase(fileType)) {
            return true;
        }
        if("image/png".equalsIgnoreCase(fileType)) {
            return true;
        }
        return false;
    }
    
    /**
     * 得到缩略图宽度
     * @param width
     * @return
     */
    private short getThumbWidth(int width) {
        if(width <= 119) {
            return (short)width;
        }
        return 119;
    }
    
    /**
     * 得到缩略图宽度
     * @param width
     * @return
     */
    private short getThumbHeight(int height) {
        if(height <= 81) {
            return (short)height;
        }
        return 81;
    }
    
    /**
     * 得到文件扩展名
     * @param fileName
     * @return
     */
    private String getFileExtension(String fileName) {
        String[] nameSplit = fileName.split("[.]");
        return nameSplit[nameSplit.length - 1];
    }
    
}
