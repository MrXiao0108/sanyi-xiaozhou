package com.dzics.data.pub.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.data.common.base.model.page.PageLimit;
import com.dzics.data.common.base.model.page.PageLimitBase;
import com.dzics.data.common.base.vo.Result;
import com.dzics.data.pub.model.dto.TcpDescValue;
import com.dzics.data.pub.model.entity.SysCmdTcp;
import com.dzics.data.pub.model.vo.AddAndUpdCmdTcpVo;
import com.dzics.data.pub.model.vo.CmdTcpItemVo;

import java.util.List;

/**
 * <p>
 * tcp 指令标识表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
public interface SysCmdTcpService extends IService<SysCmdTcp> {
    String convertTcp(String b562, String b5621);

    Result<SysCmdTcp> add (String sub, AddAndUpdCmdTcpVo cmdTcpVo);

    Result del(Integer id);

    Result update(AddAndUpdCmdTcpVo cmdTcpVo);

    Result getById(Integer id);

    Result listItem(PageLimit pageLimit, String id);

    Result<SysCmdTcp> addItem(CmdTcpItemVo cmdTcpItemVo);

    Result delItem(String id);

    Result<SysCmdTcp> updateItem(CmdTcpItemVo cmdTcpItemVo);

    Result<SysCmdTcp> getByIdItem(String id);

    Result<List<SysCmdTcp>> list(PageLimitBase pageLimit, String tcpName, String tcpValue, Integer tcpType, String tcpDescription, Integer deviceType);
    /**
     * 根据指令获取指令的 所有值类型
     * @param cmd  A562
     * @param cmdValue
     * @return
     */
    List<TcpDescValue> getCmdTcp(String cmd, String cmdValue);
}
