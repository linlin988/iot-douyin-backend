# 项目接口文档


**简介**:项目接口文档


**HOST**:http://121.41.228.22:8082


**联系人**:


**Version**:1.0.0


**接口路径**:/content/v3/api-docs?group=default


[TOC]






# 评论模块


## listComment


**接口地址**:`/content/comment/list/{id}`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>获取当前评论列表</p>



**请求示例**:


```javascript
{
  "pageNo": 1,
  "pageSize": 10,
  "sortBy": "",
  "isAsc": true
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||
|pageQuery|分页查询实体|body|true|pageQuery|pageQuery|
|&emsp;&emsp;pageNo|页码||false|integer(int64)||
|&emsp;&emsp;pageSize|页大小||false|integer(int64)||
|&emsp;&emsp;sortBy|排序字段||false|string||
|&emsp;&emsp;isAsc|是否升序||false|boolean||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|Result|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|total||integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"total": 0
}
```


## addComment


**接口地址**:`/content/comment/add`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>发布评论</p>



**请求示例**:


```javascript
{
  "videoId": 0,
  "content": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|commentDTO|CommentDTO|body|true|CommentDTO|CommentDTO|
|&emsp;&emsp;videoId|||false|integer(int64)||
|&emsp;&emsp;content|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|Result|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|total||integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"total": 0
}
```


## deleteComment


**接口地址**:`/content/comment/delete/{id}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>删除我的评论</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|Result|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|total||integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"total": 0
}
```


# 关注模块


## 关注或取关


**接口地址**:`/content/follow/{id}/{follow}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||
|follow||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|Result|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|total||integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"total": 0
}
```


## test


**接口地址**:`/content/follow/test`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|Result|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|total||integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"total": 0
}
```


## 查询是否关注


**接口地址**:`/content/follow/queryFollow/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|Result|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|total||integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"total": 0
}
```


## 分页查询我的关注列表


**接口地址**:`/content/follow/followList`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|pageQuery|分页查询实体|query|true|pageQuery|pageQuery|
|&emsp;&emsp;pageNo|页码||false|integer(int64)||
|&emsp;&emsp;pageSize|页大小||false|integer(int64)||
|&emsp;&emsp;sortBy|排序字段||false|string||
|&emsp;&emsp;isAsc|是否升序||false|boolean||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|Result|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|total||integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"total": 0
}
```


## 获取关注数量


**接口地址**:`/content/follow/followCount`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|Result|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|total||integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"total": 0
}
```


## 分页查询我的粉丝列表


**接口地址**:`/content/follow/fansList`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|pageQuery|分页查询实体|query|true|pageQuery|pageQuery|
|&emsp;&emsp;pageNo|页码||false|integer(int64)||
|&emsp;&emsp;pageSize|页大小||false|integer(int64)||
|&emsp;&emsp;sortBy|排序字段||false|string||
|&emsp;&emsp;isAsc|是否升序||false|boolean||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|Result|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|total||integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"total": 0
}
```


## 获取粉丝数量


**接口地址**:`/content/follow/fansCount`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|Result|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|total||integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"total": 0
}
```


# 视频模块


## 视频上传


**接口地址**:`/content/video/upload`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|title||query|true|string||
|description||query|true|string||
|userId||header|true|integer(int64)||
|file||query|true|file||
|coverfile||query|true|file||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|Result|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|total||integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"total": 0
}
```


## 视频列表查询


**接口地址**:`/content/video/list`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|page||query|false|integer(int32)||
|size||query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|Result|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|total||integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"total": 0
}
```


## 视频详情查询与统计


**接口地址**:`/content/video/detail/{videoId}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|videoId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|Result|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|total||integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"total": 0
}
```


# like-controller


## 点赞视频


**接口地址**:`/content/like/{id}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|Result|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|total||integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"total": 0
}
```


## 查询视频点赞数


**接口地址**:`/content/like/likeCount/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|Result|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|total||integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"total": 0
}
```


## 判断当前用户是否点赞该视频


**接口地址**:`/content/like/isLike/{videoId}/{userId}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|videoId||path|true|integer(int64)||
|userId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|Result|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||
|total||integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {},
	"total": 0
}
```