package jp.co.tis.opal.web.common.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nablarch.fw.Handler;
import nablarch.fw.web.HttpMethodBinding;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.servlet.ServletExecutionContext;
import nablarch.fw.web.servlet.WebFrontController;

import jp.co.tis.opal.web.common.rest.BodyReaderHttpServletRequestWarpper;

/**
 * アプリケーションサーバにデプロイして使用するリクエストコントローラ。
 * <pre>
 * 本フレームワークをTomcat/Websphere等のアプリケーションサーバ上で使用する際に、
 * サーブレットフィルタとしてデプロイして使用するリクエストエントリポイントである。
 * 各HTTPリクエスト毎に下記の処理を行う。
 *   1.HttpServletRequestオブジェクトを{@link jp.co.tis.opal.web.common.rest.BodyReaderHttpServletRequestWarpper BodyReaderHttpServletRequestWarpper}でラップした
 *     HttpRequest, ExecutionContext オブジェクトを生成する。
 *   2.それらを引数としてリクエストプロセッサに処理を委譲する。
 *   3.その結果(HttpResponseオブジェクトの内容)に従って、
 *     HTTPクライアントに対するレスポンス処理を行う。
 * リクエストプロセッサの初期化処理は、本クラスのサブクラスを作成し、
 * オーバライドしたinit()メソッドの中で行う。
 * 本サーブレットフィルタに処理が委譲された場合、必ずレスポンスかフォーワードを行う。
 * このため、後続のサーブレットフィルタチェインに処理が委譲されることは無い。
 * </pre>
 * @author 張
 * @since 1.0
 */
public class OpalWebFrontController extends WebFrontController {
    /** ハンドラキュー */
    @SuppressWarnings("rawtypes")
    private List<Handler> handlerQueue = new ArrayList<Handler>();
    /** フィルタ設定 */
    private FilterConfig config = null;

    /**
     * デフォルトコンストラクタ
     */
    public OpalWebFrontController() {
        setMethodBinder(new HttpMethodBinding.Binder());
    }

    /**
     * <pre>
     * 本クラスの実装では、HTTPリクエスト毎に下記の処理を行う。
     *   1. HttpServletRequestオブジェクトを
     *      {@link jp.co.tis.opal.web.common.rest.BodyReaderHttpServletRequestWarpper BodyReaderHttpServletRequestWarpper}でラップした
     *      HttpRequest, ExecutionContext オブジェクトを生成する。
     *   2. それらを引数としてリクエストプロセッサに処理を委譲する。
     *   3. その結果(HttpResponseオブジェクトの内容)に従って、
     *      HTTPクライアントに対するレスポンス処理を行う。
     * </pre>
     *
     * @param servletRequest
     *            サーブレットリクエスト
     * @param servletResponse
     *            サーブレットリスポンス
     * @param chain
     *            チェーン
     * @throws ServletException
     *             サーブレット実行時例外
     * @throws IOException
     *             IO実行時例外。
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws ServletException, IOException {

        ServletRequest requestWrapper = null;
        // サーブレットリクエストのラッパー
        requestWrapper = new BodyReaderHttpServletRequestWarpper((HttpServletRequest) servletRequest);

        ServletExecutionContext context = new ServletExecutionContext((HttpServletRequest) requestWrapper,
                (HttpServletResponse) servletResponse, this.config.getServletContext());

        HttpRequest request = context.getHttpRequest();
        context.setHandlerQueue(this.handlerQueue).handleNext(request);
    }

    /**
     * 本クラスの実装では、リポジトリ上にコンポーネント"webFrontController"
     * が存在すれば、そのインスタンスを以降の処理で使用する。 存在しない場合は、このインスタンスをそのまま使用する。
     */
    @Override
    public void init(FilterConfig config) {
        this.config = config;
    }

    /**
     * ハンドラリストを取得
     *
     * @return ハンドラリスト
     */
    @SuppressWarnings("rawtypes")
    @Override
    public List<Handler> getHandlerQueue() {
        return this.handlerQueue;
    }

    /**
     * サーブレットフィルタの設定情報を設定する.
     *
     * @param config
     *            設定情報
     */
    @Override
    public void setServletFilterConfig(FilterConfig config) {
        this.config = config;
    }

    /**
     * サーブレットフィルタの設定情報を取得する。
     *
     * @return 設定情報
     */
    @Override
    public FilterConfig getServletFilterConfig() {
        return this.config;
    }

    /**
     * 本クラスのdestroy()メソッドでは何も行わない。
     */
    @Override
    public void destroy() {
        this.config = null;
    }

}
