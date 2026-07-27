package com.ruoyi.web.controller.knowledge;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.knowledge.domain.KosRelationRule;
import com.ruoyi.system.knowledge.domain.KosTerm;
import com.ruoyi.system.knowledge.dto.KosExtractRequest;
import com.ruoyi.system.knowledge.dto.KosExtractResponse;
import com.ruoyi.system.knowledge.service.IKosExtractionService;

@RestController
@RequestMapping("/knowledge/kos")
public class KosExtractionController extends BaseController
{
    @Autowired
    private IKosExtractionService kosService;

    @PreAuthorize("@ss.hasPermi('knowledge:kos:extract')")
    @Log(title = "KOS知识抽取", businessType = BusinessType.INSERT)
    @PostMapping("/extract")
    public AjaxResult extract(@Validated @RequestBody KosExtractRequest request)
    {
        KosExtractResponse response = kosService.extract(request);
        return success(response);
    }

    @PreAuthorize("@ss.hasPermi('knowledge:kos:query')")
    @Log(title = "刷新KOS缓存", businessType = BusinessType.OTHER)
    @PostMapping("/refresh")
    public AjaxResult refresh()
    {
        kosService.refreshKosCache();
        return success("KOS缓存已刷新");
    }

    @PreAuthorize("@ss.hasPermi('knowledge:kos:query')")
    @GetMapping("/terms")
    public AjaxResult listTerms(KosTerm query)
    {
        List<KosTerm> list = kosService.listTerms(query);
        return success(list);
    }

    @PreAuthorize("@ss.hasPermi('knowledge:kos:query')")
    @GetMapping("/terms/{termId}")
    public AjaxResult getTerm(@PathVariable String termId)
    {
        KosTerm term = kosService.getTerm(termId);
        return success(term);
    }

    @PreAuthorize("@ss.hasPermi('knowledge:kos:add')")
    @Log(title = "新增KOS词条", businessType = BusinessType.INSERT)
    @PostMapping("/terms")
    public AjaxResult addTerm(@Validated @RequestBody KosTerm term)
    {
        return toAjax(kosService.insertTerm(term));
    }

    @PreAuthorize("@ss.hasPermi('knowledge:kos:edit')")
    @Log(title = "修改KOS词条", businessType = BusinessType.UPDATE)
    @PutMapping("/terms")
    public AjaxResult updateTerm(@Validated @RequestBody KosTerm term)
    {
        return toAjax(kosService.updateTerm(term));
    }

    @PreAuthorize("@ss.hasPermi('knowledge:kos:remove')")
    @Log(title = "删除KOS词条", businessType = BusinessType.DELETE)
    @DeleteMapping("/terms/{termId}")
    public AjaxResult deleteTerm(@PathVariable String termId)
    {
        return toAjax(kosService.deleteTerm(termId));
    }

    @PreAuthorize("@ss.hasPermi('knowledge:kos:query')")
    @GetMapping("/rules")
    public AjaxResult listRules(KosRelationRule query)
    {
        List<KosRelationRule> list = kosService.listRules(query);
        return success(list);
    }

    @PreAuthorize("@ss.hasPermi('knowledge:kos:query')")
    @GetMapping("/rules/{ruleId}")
    public AjaxResult getRule(@PathVariable String ruleId)
    {
        KosRelationRule rule = kosService.getRule(ruleId);
        return success(rule);
    }

    @PreAuthorize("@ss.hasPermi('knowledge:kos:add')")
    @Log(title = "新增KOS关系规则", businessType = BusinessType.INSERT)
    @PostMapping("/rules")
    public AjaxResult addRule(@Validated @RequestBody KosRelationRule rule)
    {
        return toAjax(kosService.insertRule(rule));
    }

    @PreAuthorize("@ss.hasPermi('knowledge:kos:edit')")
    @Log(title = "修改KOS关系规则", businessType = BusinessType.UPDATE)
    @PutMapping("/rules")
    public AjaxResult updateRule(@Validated @RequestBody KosRelationRule rule)
    {
        return toAjax(kosService.updateRule(rule));
    }

    @PreAuthorize("@ss.hasPermi('knowledge:kos:remove')")
    @Log(title = "删除KOS关系规则", businessType = BusinessType.DELETE)
    @DeleteMapping("/rules/{ruleId}")
    public AjaxResult deleteRule(@PathVariable String ruleId)
    {
        return toAjax(kosService.deleteRule(ruleId));
    }
}
