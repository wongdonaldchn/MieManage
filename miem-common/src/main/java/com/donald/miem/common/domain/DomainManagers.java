package com.donald.miem.common.domain;

import nablarch.core.validation.ee.DomainManager;

/**
 * {@link DomainManagers} の実装クラス。
 * <p/>
 * ドメインを定義したBeanクラスを返却する。
 *
 * @author 唐
 * @since 1.0
 */
public final class DomainManagers implements DomainManager<DomainBean> {

    @Override
    public Class<DomainBean> getDomainBean() {
        // ドメインBeanのClassオブジェクトを返す。
        return DomainBean.class;
    }
}
