package com.iot.content.controller;

import com.iot.commonModules.DTO.pageQuery;
import com.iot.commonModules.common.Result;


import com.iot.content.service.IFollowService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/follow")
@Tag(name = "关注模块",description = "关注接口")
public class FollowController {

    @Autowired
    private IFollowService followService;

    @GetMapping("/test")//为网关转发测试用，可删
    public Result test(){return Result.success();}
    /**
     * 关注或者取关
     * @param followedUserId
     * @param follow
     * @return
     */
    @Operation(summary = "关注或取关")
    @PutMapping("/{id}/{follow}")
    public Result follow(@PathVariable ("id") Long followedUserId,@PathVariable ("follow")  Boolean follow){
       log.info("被关注者ID：{}", followedUserId);
        return followService.add(followedUserId,follow);
    }

    /**
     * 查询是否关注
     * @param followedUserId
     * @return
     */
    @Operation(summary = "查询是否关注")
    @GetMapping("/queryFollow/{id}")
    public Result queryFollow(@PathVariable ("id") Long followedUserId){
        return followService.queryFollow(followedUserId);
    }

    /**
     * 分页查询我的关注列表
     * @param pageQuery
     * @return
     */
   @Operation(summary = "分页查询我的关注列表")
   @GetMapping("/followList")
   public Result followList(@RequestBody pageQuery pageQuery){
       return followService.getMyFollowList(pageQuery);
   }

   /**
    * 分页查询我的粉丝列表
    * @param pageQuery
    * @return
    */
   @Operation(summary = "分页查询我的粉丝列表")
   @GetMapping("/fansList")
   public Result fansList(@RequestBody pageQuery pageQuery){
       return followService.getMyFansList(pageQuery);
   }

   @Operation(summary = "获取关注数量")
   @GetMapping("/followCount")
   public Result followCount(){
       return followService.getFollowCountByUserId();
   }

   @Operation(summary = "获取粉丝数量")
   @GetMapping("/fansCount")
   public Result fansCount(){
       return followService.getFansCountByUserId();
   }

}
