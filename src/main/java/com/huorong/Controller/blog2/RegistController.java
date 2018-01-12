package com.huorong.Controller.blog2;

import com.huorong.domain.Result;
import com.huorong.service.RegistService;
import com.huorong.utils.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("regist")
public class RegistController {
    private Logger log = LoggerFactory.getLogger(RegistController.class);
    @Autowired
    RegistService registService;

    @RequestMapping("toRegist")
    public Result regist(@RequestParam Map params) {
        String result = registService.registResult(params);
        String userId = MapUtils.getStr(params, "userId");
        if ("2".equals(result)) {
            try {
                result = registService.sendEmailToReigst(MapUtils.getStr(params, "blogEmail"), userId);
            } catch (Exception e) {
                log.error("邮件记录日志异常" + e);
            }
        }
        return Result.build("0", "ok", MapUtils.of(result));
    }

    @RequestMapping(value = "toActive", method = RequestMethod.GET)
    public void exportByJedis(@RequestParam String uuid) throws Exception {
        registService.toActive(uuid);
    }
}
