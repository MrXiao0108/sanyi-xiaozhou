package com.dzics.data.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.data.business.model.ProductMaterial;
import com.dzics.data.business.service.ProductService;
import com.dzics.data.common.base.enums.Message;
import com.dzics.data.common.base.enums.UserIdentityEnum;
import com.dzics.data.common.base.exception.enums.CustomExceptionType;
import com.dzics.data.common.base.model.dto.MaterialVo;
import com.dzics.data.common.base.model.constant.FinalCode;
import com.dzics.data.common.base.model.vo.ImgDo;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.common.util.LineTypeUtil;
import com.dzics.data.common.util.UnderlineTool;
import com.dzics.data.pms.db.dao.DzProductDao;
import com.dzics.data.pms.db.dao.DzProductDetectionTemplateDao;
import com.dzics.data.pms.model.dto.AddProductVo;
import com.dzics.data.pms.model.dto.ProductListModel;
import com.dzics.data.pms.model.entity.DzMaterial;
import com.dzics.data.pms.model.entity.DzProduct;
import com.dzics.data.pms.model.entity.DzProductDetectionTemplate;
import com.dzics.data.pms.service.DzMaterialService;
import com.dzics.data.ums.db.dao.SysDepartDao;
import com.dzics.data.ums.model.entity.SysUser;
import com.dzics.data.ums.service.DzicsUserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xnb
 * @date 2022年02月10日 11:20
 */
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    @Autowired
    DzicsUserService sysUserServiceDao;
    @Autowired
    DzProductDao dzProductMapper;
    @Autowired
    SysDepartDao sysDepartMapper;
    @Autowired
    DzMaterialService dzMaterialService;
    @Autowired
    DzProductDetectionTemplateDao dzProductDetectionTemplateMapper;

    @Override
    public Result<List<DzProduct>> list(String sub, ProductListModel productListModel) {
        SysUser user = sysUserServiceDao.getByUserName(sub);
        if (user.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue() && user.getUseOrgCode().equals(FinalCode.DZ_USE_ORG_CODE)) {
            user.setUseOrgCode(null);
        }
        if (productListModel.getPage() != -1) {
            PageHelper.startPage(productListModel.getPage(), productListModel.getLimit());
        }
        if (!StringUtils.isEmpty(productListModel.getField())) {
            productListModel.setField(UnderlineTool.humpToLine(productListModel.getField()));
        }

        List<DzProduct> dzProducts = dzProductMapper.listProduct(productListModel.getField(), productListModel.getType(), productListModel.getProductName(), user.getUseOrgCode(),productListModel.getLineType());
        PageInfo<DzProduct> info = new PageInfo<>(dzProducts);
        return new Result(CustomExceptionType.OK, info.getList(), info.getTotal());
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result add(String sub, AddProductVo addProductVo) {
        String lineType = addProductVo.getLineType();
        LineTypeUtil.typtIsOk(lineType);
        //名称重复判断
        if (isExistProduct(1, addProductVo)) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_59);
        }
        //序号重复判断
        if (isExistProduct(2, addProductVo)) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_60);
        }
        //判断产品简码
        if (isExistProduct(3, addProductVo)) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_75);
        }
        //判断产品物料编码
        if (isExistProduct(4, addProductVo)) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_76);
        }
        //判断组件信息
        List<MaterialVo> materialList = addProductVo.getMaterialList();
        if(materialList==null||materialList.size()==0){
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_77);
        }
        //组件简码去重判断
        List<String> materialAlias = materialList.stream().map(p -> p.getMaterialAlias()).collect(Collectors.toList());
        List<String> materialAlias1 = materialAlias.stream().distinct().collect(Collectors.toList());
        if(materialAlias.size()!=materialAlias1.size()){
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_78);
        }
        //组件物料号去重判断
        List<String> materialNo = materialList.stream().map(p -> p.getMaterialNo()).collect(Collectors.toList());
        List<String> materialNo1 = materialNo.stream().distinct().collect(Collectors.toList());
        if(materialNo.size()!=materialNo1.size()){
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_79);
        }
        String img = null;
        //图片非空判断
        if (addProductVo.getPictureList() == null || addProductVo.getPictureList().size() == 0 || addProductVo.getPictureList().get(0) == null) {
            img = FinalCode.DZ_PRODUCT;
        } else {
            img = addProductVo.getPictureList().get(0).getUrl();
        }
        if (img == null) {
            img = FinalCode.DZ_PRODUCT;
        }
        DzProduct dzProduct = new DzProduct();
        dzProduct.setDepartId(addProductVo.getDepartId());
        dzProduct.setDepartOrgCode(addProductVo.getOrgCode());
        dzProduct.setProductName(addProductVo.getProductName());
        dzProduct.setProductNo(addProductVo.getProductNo());
        dzProduct.setOrgCode(addProductVo.getOrgCode());
        dzProduct.setPicture(img);//产品图片
        dzProduct.setSyCategory(addProductVo.getSyCategory());
        dzProduct.setSyProductAlias(addProductVo.getSyProductAlias());
        dzProduct.setSyProductNo(addProductVo.getSyProductNo());
        dzProduct.setRemarks(addProductVo.getRemarks());
        dzProduct.setLineType(addProductVo.getLineType());
        dzProduct.setFrequency(addProductVo.getFrequency());
        int insert = dzProductMapper.insert(dzProduct);
        if (insert == 1) {
            List<DzMaterial>list=new ArrayList();
            for (MaterialVo materialVo:materialList) {
                DzMaterial dzMaterial=new DzMaterial();
                dzMaterial.setProductId(dzProduct.getProductId());
                dzMaterial.setMaterialAlias(materialVo.getMaterialAlias());
                dzMaterial.setMaterialNo(materialVo.getMaterialNo());
                dzMaterial.setOrgCode(dzProduct.getOrgCode());
                list.add(dzMaterial);
            }
            dzMaterialService.saveBatch(list);
            return new Result(CustomExceptionType.OK, dzProduct);
        }
        return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_1);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result put(String sub, AddProductVo addProductVo) {
        String lineType = addProductVo.getLineType();
        LineTypeUtil.typtIsOk(lineType);
        if (addProductVo.getProductId() == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_57);
        }
        DzProduct dzProduct = dzProductMapper.selectById(addProductVo.getProductId());
        String productNo = dzProduct.getProductNo();//旧的产品id
        if (dzProduct == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_58);
        }
        addProductVo.setDepartId(dzProduct.getDepartId());
        //产品名更改了 检测新名称是否存在
        if (!dzProduct.getProductName().equals(addProductVo.getProductName())) {
            if (isExistProduct(1, addProductVo)) {
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_59);
            }
        }
        //产品序号(id)更改了 检测新序号是否存在
        if (!dzProduct.getProductNo().equals(addProductVo.getProductNo())) {
            if (isExistProduct(2, addProductVo)) {
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_60);
            }
        }
        //产品编码去重判断
        if (!dzProduct.getSyProductAlias().equals(addProductVo.getSyProductAlias())) {
            if (isExistProduct(3, addProductVo)) {
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_75);
            }
        }
        //产品物料编码去重判断
        if (!dzProduct.getSyProductNo().equals(addProductVo.getSyProductNo())) {
            if (isExistProduct(4, addProductVo)) {
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_76);
            }
        }
        //组件简码去重判断
        List<MaterialVo> materialList = addProductVo.getMaterialList();
        if(materialList==null||materialList.size()==0){
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_77);
        }
        List<String> materialAlias = materialList.stream().map(p -> p.getMaterialAlias()).collect(Collectors.toList());
        List<String> materialAlias1 = materialAlias.stream().distinct().collect(Collectors.toList());
        if(materialAlias.size()!=materialAlias1.size()){
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_78);
        }
        //组件物料号去重判断
        List<String> materialNo = materialList.stream().map(p -> p.getMaterialNo()).collect(Collectors.toList());
        List<String> materialNo1 = materialNo.stream().distinct().collect(Collectors.toList());
        if(materialNo.size()!=materialNo1.size()){
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_79);
        }
        String img = "";
        //图片非空判断
        if (addProductVo.getPictureList() == null || addProductVo.getPictureList().size() == 0 || addProductVo.getPictureList().get(0) == null) {
            img = FinalCode.DZ_PRODUCT;
        } else {
            img = addProductVo.getPictureList().get(0).getUrl();
        }
        dzProduct.setPicture(img);
        dzProduct.setProductNo(addProductVo.getProductNo());
        dzProduct.setProductName(addProductVo.getProductName());
        dzProduct.setSyProductNo(addProductVo.getSyProductNo());
        dzProduct.setSyProductAlias(addProductVo.getSyProductAlias());
        dzProduct.setSyCategory(addProductVo.getSyCategory());
        dzProduct.setLineType(addProductVo.getLineType());
        dzProduct.setFrequency(addProductVo.getFrequency());
        int i = dzProductMapper.updateById(dzProduct);
        if (i > 0 && !productNo.equals(addProductVo.getProductNo())) {
            //如果旧的产品id和新的产品id不一样 则id更改了  相关联的数据也要更改
            Integer sum = dzProductDetectionTemplateMapper.updateTemplate(productNo, addProductVo.getProductNo());
        }
        //清除旧的组件物料
        dzMaterialService.remove(new QueryWrapper<DzMaterial>().eq("product_id",dzProduct.getProductId()));
        //添加新的组件物料
        List<DzMaterial>list=new ArrayList();
        for (MaterialVo materialVo:materialList) {
            DzMaterial dzMaterial=new DzMaterial();
            dzMaterial.setProductId(dzProduct.getProductId());
            dzMaterial.setMaterialAlias(materialVo.getMaterialAlias());
            dzMaterial.setMaterialNo(materialVo.getMaterialNo());
            dzMaterial.setOrgCode(dzProduct.getOrgCode());
            list.add(dzMaterial);
        }
        dzMaterialService.saveBatch(list);
        return new Result(CustomExceptionType.OK, dzProduct);
    }

    @Override
    public Result del(String sub, Long productId) {
        DzProduct dzProduct = dzProductMapper.selectById(productId);
        if (dzProduct == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_58);
        }
        //删除判断，产品是否绑定检测项
        QueryWrapper<DzProductDetectionTemplate> eq = new QueryWrapper<DzProductDetectionTemplate>().eq("product_no", dzProduct.getProductNo());
        List<DzProductDetectionTemplate> productNo = dzProductDetectionTemplateMapper.selectList(eq);
        if (productNo.size() > 0) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_62);
        }
        int i = dzProductMapper.deleteById(productId);
        return new Result(CustomExceptionType.OK, Message.OK_2);
    }


    @Override
    public Result getById(String sub, Long productId) {
        DzProduct dzProduct = dzProductMapper.selectById(productId);
        if(dzProduct==null){
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_6);
        }
        List<ImgDo> list = new ArrayList<>();
        ImgDo imgDo = new ImgDo();
        imgDo.setUrl(dzProduct.getPicture());
        list.add(imgDo);
        dzProduct.setPictureList(list);
        List<DzMaterial> product_id = dzMaterialService.list(new QueryWrapper<DzMaterial>().eq("product_id", productId));
        ProductMaterial productMaterial = new ProductMaterial();
        BeanUtils.copyProperties(dzProduct,productMaterial);
        productMaterial.setMaterialList(product_id);
        return new Result(CustomExceptionType.OK, productMaterial);
    }

    /**
     * 产品 添加修改  检测序号和名称是否重复
     *
     * @param type         1检测名称  2检测序号(id) 3检查产品简码 4.检查产品物料编码
     * @param addProductVo
     * @return
     */
    public boolean isExistProduct(int type, AddProductVo addProductVo) {
        QueryWrapper<DzProduct> eq = new QueryWrapper<DzProduct>();
        if (type == 1) {
            eq.eq("line_type", addProductVo.getLineType());
            eq.eq("product_name", addProductVo.getProductName());
        } else if (type == 2) {
            eq.eq("product_no", addProductVo.getProductNo());
        } else if (type == 3) {
            eq.eq("line_type", addProductVo.getLineType());
            eq.eq("sy_product_alias", addProductVo.getSyProductAlias());
        } else if (type == 4) {
            eq.eq("line_type", addProductVo.getLineType());
            eq.eq("sy_productNo", addProductVo.getSyProductNo());
        }
        List<DzProduct> dzProducts = dzProductMapper.selectList(eq);
        if (dzProducts.size() > 0) {
            return true;
        }
        return false;
    }
}
