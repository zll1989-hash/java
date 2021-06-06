package com.ejlerp.cache.controller;

import com.ejlerp.cache.api.SerialNumberService;
import com.ejlerp.cache.vo.SerialNumberVo;
import com.ejlerp.common.vo.JsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 创建或修改业务流水号接口
 *
 * @author Peter
 */
@RestController
@RequestMapping(value = "/api/wms/rest/serial")
@Api(tags = "创建或修改业务流水号接口")
public class SerialNumberController {
    @Autowired
    private SerialNumberService serialNumberService;

    @ApiOperation(value = "根据id查找规则，便于根据返回的数据修改现有的规则（无缓存）")
    @RequestMapping(value = "/{tenantId}/{id}", method = RequestMethod.GET)
    public JsonResult findById(@ApiParam(value = "tenantId", required = true) @PathVariable Long tenantId,
                               @ApiParam(value = "id", required = true) @PathVariable Long id) {
        return new JsonResult(JsonResult.SUCCESSFUL, serialNumberService.findById(tenantId, id));
    }

    @ApiOperation(value = "新增或修改")
    @PostMapping(value = "/saveOrupdate")
    public JsonResult addOrUpdate(@RequestBody SerialNumberVo serialNumberVo) {
        serialNumberService.addOrUpdate(serialNumberVo);
        return new JsonResult(JsonResult.SUCCESSFUL);
    }

    @ApiOperation(value = "删除")
    @DeleteMapping(value = "/delete/{tenantId}/{id}")
    public JsonResult delete(@ApiParam(value = "tenantId", required = true) @PathVariable Long tenantId,
                             @ApiParam(value = "id", required = true) @PathVariable Long id) {
        serialNumberService.delete(tenantId,id);
        return new JsonResult(JsonResult.SUCCESSFUL);
    }

    @ApiOperation(value = "手动清理缓存")
    @DeleteMapping(value = "/clearCache")
    public JsonResult clearCache() {
        serialNumberService.clearCache();
        return new JsonResult(JsonResult.SUCCESSFUL);
    }
}
