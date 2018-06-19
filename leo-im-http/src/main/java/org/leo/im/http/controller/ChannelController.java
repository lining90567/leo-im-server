package org.leo.im.http.controller;

import org.leo.im.api.dto.ChannelDTO;
import org.leo.im.api.dto.ChannelMemberDTO;
import org.leo.im.api.provider.ServiceFactory;
import org.leo.im.api.service.ChannelService;
import org.leo.im.common.data.Page;
import org.leo.im.http.vo.ChannelVO;
import org.leo.im.http.vo.UserChannelVO;
import org.leo.im.service.support.ServiceProxy;
import org.leo.im.util.BeanUtils;
import org.leo.web.annotation.DeleteMapping;
import org.leo.web.annotation.GetMapping;
import org.leo.web.annotation.PatchMapping;
import org.leo.web.annotation.PathVariable;
import org.leo.web.annotation.PostMapping;
import org.leo.web.annotation.PutMapping;
import org.leo.web.annotation.RequestBody;
import org.leo.web.annotation.RequestHeader;
import org.leo.web.annotation.RequestMapping;
import org.leo.web.annotation.RequestParam;
import org.leo.web.annotation.RestController;
import org.leo.web.rest.ResponseEntity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import io.netty.handler.codec.http.FullHttpRequest;

/**
 * 频道控制器
 * 
 * @author Leo
 * @date 2018/4/3
 */
@RestController
@RequestMapping("/channels")
public final class ChannelController extends BaseController {

    @PostMapping("")
    public ResponseEntity<UserChannelVO> createChannel(FullHttpRequest request, @RequestHeader("X-Token") String token,
            @RequestBody String body) {
        String userId = this.getSubjectFromJwt(token, "userId");
        ChannelDTO dto = new ChannelDTO();
        JSONObject json = JSONObject.parseObject(body);
        dto.setType(json.getString("type"));
        if ("G".equals(dto.getType())) {
            dto.setName(json.getString("name"));
            dto.setPurpose(json.getString("purpose"));
            JSONArray members = json.getJSONArray("members");
            dto.setMemberCount(members.size());
            for(int i = 0; i < members.size(); i++) {
                ChannelMemberDTO member = new ChannelMemberDTO();
                JSONObject memberJson = members.getJSONObject(i);
                member.setId(memberJson.getString("id"));
                member.setNickname(memberJson.getString("nickname"));
                member.setAdmin(memberJson.getString("id").equals(userId));
                dto.getMembers().add(member);
            }
        } 
        if ("P".equals(dto.getType())) {
            dto.setFromUserId(userId);
            dto.setFromUsername(json.getString("fromUsername"));
            dto.setFromUserNickname(json.getString("fromUserNickname"));
            dto.setToUserId(json.getString("toUserId"));
            dto.setToUsername(json.getString("toUsername"));
            dto.setToUserNickname(json.getString("toUserNickname"));
            dto.setName(dto.getToUserNickname() != null && !dto.getToUserNickname().trim().isEmpty() ? 
                    dto.getToUserNickname() : dto.getToUsername());
            dto.setMemberCount(2);
            ChannelMemberDTO member1 = new ChannelMemberDTO();
            member1.setId(userId);
            member1.setAdmin(true);
            dto.getMembers().add(member1);
            ChannelMemberDTO member2 = new ChannelMemberDTO();
            member2.setId(dto.getToUserId());
            dto.getMembers().add(member2);
        }
        dto.setCreatorId(userId);
        ChannelService serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createChannelService());
        ChannelDTO returnDTO = serviceProxy.saveChannel(dto, json.getString("creatorNickname"));
        UserChannelVO vo = new UserChannelVO();
        BeanUtils.copyProperties(returnDTO, vo);
        vo.setChannelId(returnDTO.getId());
        vo.setChannelName(returnDTO.getName());
        vo.setChannelDisplayName(getChannelDisplayName(returnDTO, userId));
        vo.setChannelType(returnDTO.getType());
        return ResponseEntity.created(vo);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ChannelVO> getById(@PathVariable("id") String id) {
        ChannelService serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createChannelService());
        ChannelDTO dto = serviceProxy.getById(id);
        if(dto == null) {
            return ResponseEntity.notFound().build();
        }
        ChannelVO vo = new ChannelVO();
        BeanUtils.copyProperties(dto, vo);
        return ResponseEntity.ok(vo);
    }
    
    
    @GetMapping("/{channelId}/isAdmin")
    public ResponseEntity<Boolean> isAdmin(@PathVariable("channelId") String channelId, @RequestHeader("X-Token") String token) {
        String userId = this.getSubjectFromJwt(token, "userId");
        ChannelService serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createChannelService());
        boolean isAdmin = serviceProxy.isAdmin(userId, channelId);
        return ResponseEntity.ok(isAdmin);
    }
    
    @PatchMapping("/{channelId}")
    public ResponseEntity<?> updateChannel(@PathVariable("channelId") String channelId, @RequestBody String body) {
        JSONObject json = JSONObject.parseObject(body);
        ChannelService serviceProxy = null;
        if(json.containsKey("name")) {
            serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createChannelService());
            int count = serviceProxy.updateName(channelId, json.getString("name"));
            return ResponseEntity.ok(count);
        }
        if(json.containsKey("purpose")) {
            serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createChannelService());
            int count = serviceProxy.updatePurpose(channelId, json.getString("purpose"));
            return ResponseEntity.ok(count);
        }
        return ResponseEntity.internalServerError("not found allowed filed in body");
    }
    
    @GetMapping("/{channelId}/members")
    public ResponseEntity<Page<ChannelMemberDTO>> listMember(@PathVariable("channelId") String channelId, 
            @RequestParam("username") String username, @RequestParam("limit") int limit, @RequestParam("offset") int offset) {
        ChannelService serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createChannelService());
        Page<ChannelMemberDTO> result = serviceProxy.listMember(channelId, username, limit, offset);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/{channelId}/members")
    public ResponseEntity<Integer> addMember(@PathVariable("channelId") String channelId, @RequestBody String body) {
        JSONObject data = JSONObject.parseObject(body);
        JSONArray jsonArray = data.getJSONArray("users");
        String[] userIds = new String[jsonArray.size()];
        String[] userNicknames = new String[jsonArray.size()];
        for(int i = 0; i < userIds.length; i++) {
            userIds[i] = jsonArray.getJSONObject(i).getString("id");
            userNicknames[i] = jsonArray.getJSONObject(i).getString("nickname");
        }
        ChannelService serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createChannelService());
        int count = serviceProxy.addMember(channelId, userIds, userNicknames, data.getString("admin"));
        return ResponseEntity.created(count);
    }
    
    @DeleteMapping("/{channelId}/members")
    public ResponseEntity<Integer> removeMember(@PathVariable("channelId") String channelId, @RequestBody String body) {
        ChannelService serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createChannelService());
        JSONObject data = JSONObject.parseObject(body);
        int count = serviceProxy.removeMember(channelId, data.getString("memberId"), data.getString("memberNickname"),
                data.getString("admin"));
        if(count > 0) {
            return ResponseEntity.noContent(count);
        }
        return ResponseEntity.notFound().build();
    }
    
    @PutMapping("/{channelId}/admin")
    public ResponseEntity<Integer> changeAdmin(@PathVariable("channelId") String channelId, @RequestBody String body) {
        JSONObject json = JSONObject.parseObject(body);
        ChannelService serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createChannelService());
        int count = serviceProxy.changeAdmin(channelId, json.getString("memberId"), json.getBooleanValue("isAdmin"));
        return ResponseEntity.created(count);
    }
    
    /**
     * 离开频道
     * @param channelId
     * @param body
     * @return
     */
    @DeleteMapping("/{channelId}/members/{memberId}")
    public ResponseEntity<Integer> leaveChannel(@PathVariable("channelId") String channelId, @PathVariable("memberId") String memberId,
            @RequestBody String body) {
        JSONObject data = JSONObject.parseObject(body);
        ChannelService serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createChannelService());
        int count = serviceProxy.leaveChannel(channelId, memberId, data.getString("memberNickname"));
        return ResponseEntity.noContent(count);
    }
    
    @DeleteMapping("/{channelId}")
    public ResponseEntity<Integer> removeChannel(@PathVariable("channelId") String channelId, @RequestHeader("X-Token") String token) {
        String userId = this.getSubjectFromJwt(token, "userId");
        ChannelService serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createChannelService());
        int count = serviceProxy.removeChannel(channelId, userId);
        return ResponseEntity.noContent(count);
    }
    
    /**
     * 得到频道的显示名称
     * @param dto
     * @param creatorId
     * @return
     */
    private String getChannelDisplayName(ChannelDTO dto, String creatorId) {
        if(dto.getType().equals("G")) {
            return dto.getName();
        }
        if(dto.getCreatorId().equals(creatorId)) {
            return dto.getToUserNickname();
        }
        return dto.getFromUserNickname();
    }

}
