package com.dzics.data.appoint.changsha.mom.service;

import com.dzics.data.appoint.changsha.mom.model.dto.MaterialFrameRes;
import com.dzics.data.appoint.changsha.mom.model.dto.MomResult;
import com.dzics.data.appoint.changsha.mom.model.dto.SearchDzdcMomSeqenceNo;
import com.dzics.data.appoint.changsha.mom.model.dto.agv.MomResultSearch;
import com.dzics.data.common.base.vo.Result;

/**
 * MOM接口调用基础服务
 *
 * @author: van
 * @since: 2022-07-07
 */
public interface MOMBaseService {

    /**
     * 查询料框参数接口
     *
     * @param innerGroupId:
     * @param groupId:
     * @param orderNo:
     * @param lineNo:
     * @param sourceNo:
     * @param paramRsrv1:
     * @return :
     */
    MaterialFrameRes getStringPalletType(String innerGroupId, String groupId, String orderNo, String lineNo, String sourceNo, String paramRsrv1);

    /**
     * 获取下个工序号
     * @param dzdcMomSeqenceNo:
     * @return :
     * @author van
     * @date 2022/7/12
     */
    MomResultSearch getSanyMomNextSpecNo(SearchDzdcMomSeqenceNo dzdcMomSeqenceNo);

    /**
     * 请求MOM更新 料点状态
     *
     *
     * @param innerGroupId:
     * @param groupId:
     * @param lineNo:
     * @param orderCode:
     * @param externalCode:
     * @param palletNo:
     * @return :
     */
    boolean updatePointPallet(String innerGroupId, String groupId, String lineNo, String orderCode, String externalCode, String palletNo);


    /**
     * 发送请求
     *
     * @param url:
     * @param reqJson:
     * @param responseType:
     * @return ：
     * @author van
     * @date 2022/7/7
     */
    <T> T sendPost(String url, String reqJson, Class<T> responseType);

    Result searechOprSequenceNo(SearchDzdcMomSeqenceNo momSeqenceNo);
}
