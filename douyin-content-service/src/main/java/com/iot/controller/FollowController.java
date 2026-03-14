package com.iot.controller;

import com.iot.commonModules.DTO.pageQuery;
import com.iot.commonModules.common.Result;

import com.iot.service.IFollowService;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/follow")
@Slf4j
@Tag(name = "关注模块",description = "关注接口")
public class FollowController {

    @Autowired
    private IFollowService followService;
    /**
     * 关注或者取关
     * @param followedUserId
     * @param follow
     * @return
     */
    @Operation(summary = "关注或取关")
    @PutMapping("/{id}/{follow}")
    public Result follow(@PathVariable ("id") Long followedUserId,@PathVariable ("follow")  Boolean follow){
       return followService.add(followedUserId,follow);
    }

    /**
     * 查询是否关注
     * @param followedUserId
     * @return
     */
    @Operation(summary = "查询是否关注")
    @GetMapping("/queryFollow/{id}")
    public Result queryFollow(@PathVariable Long followedUserId){
        return followService.queryFollow(followedUserId);
    }

    /**
     * 分页查询我的关注列表
     * @param pageQuery
     * @return
     */
   @Operation(summary = "分页查询我的关注列表")
   @GetMapping("/followList")
   public Result followList(pageQuery pageQuery){
       return followService.getMyFollowList(pageQuery);
   }

   /**
    * 分页查询我的粉丝列表
    * @param pageQuery
    * @return
    */
   @Operation(summary = "分页查询我的粉丝列表")
   @GetMapping("/fansList")
   public Result fansList(pageQuery pageQuery){
       return followService.getMyFansList(pageQuery);
   }















}
