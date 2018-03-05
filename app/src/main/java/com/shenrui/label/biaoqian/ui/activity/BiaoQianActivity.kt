package com.shenrui.label.biaoqian.ui.activity

import com.shenrui.label.biaoqian.R
import com.shenrui.label.biaoqian.mvp.base.BaseActivity
import com.shenrui.label.biaoqian.mvp.contract.BiaoQianContract
import com.shenrui.label.biaoqian.mvp.presenter.BiaoQianPresenter

class BiaoQianActivity : BaseActivity<BiaoQianContract.View,
        BiaoQianPresenter<BiaoQianContract.View>>(),
        BiaoQianContract.View {
    override fun <E> onError(e: E?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <D> onSuccess(d: D?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showLoading() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun dismissLoading() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun verificationCode(code: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSuccessBanner(code: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun layoutId() = R.layout.activity_biao_qian

    override fun createPresenter(): BiaoQianPresenter<BiaoQianContract.View>? {
        return BiaoQianPresenter()
    }
    override fun initData() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initListener() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

//    fun add(fragment: BaseLibFragment, id: Int, tag: String) {
//        var fragment = fragment
//        val fragmentManager = (mContext as BaseLibActivity).getSupportFragmentManager()
//        val fragmentTransaction = fragmentManager.beginTransaction()
//        //优先检查，fragment是否存在，避免重叠
//        val tempFragment = fragmentManager.findFragmentByTag(tag) as BaseLibFragment
//        if (EmptyUtils.isNotEmpty(tempFragment)) {
//            fragment = tempFragment
//        }
//        if (fragment.isAdded()) {
//            addOrShowFragment(fragmentTransaction, fragment, id, tag)
//        } else {
//            if (currentFragment != null && currentFragment.isAdded()) {
//                fragmentTransaction.hide(currentFragment).add(id, fragment, tag).commit()
//            } else {
//                fragmentTransaction.add(id, fragment, tag).commit()
//            }
//            currentFragment = fragment
//        }
//    }
//
//    /**
//     * 添加或者显示 fragment
//     *
//     * @param fragment
//     */
//    private fun addOrShowFragment(transaction: FragmentTransaction, fragment: BaseLibFragment, id: Int, tag: String) {
//        if (currentFragment === fragment)
//            return
//        if (!fragment.isAdded()) { // 如果当前fragment未被添加，则添加到Fragment管理器中
//            transaction.hide(currentFragment).add(id, fragment, tag).commit()
//        } else {
//            transaction.hide(currentFragment).show(fragment).commit()
//        }
//        currentFragment.setUserVisibleHint(false)
//        currentFragment = fragment
//        currentFragment.setUserVisibleHint(true)
//    }
}
