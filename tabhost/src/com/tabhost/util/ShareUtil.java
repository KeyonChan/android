package com.tabhost.util;

import java.io.File;

import com.tabhost.R;
import com.umeng.socialize.bean.MultiStatus;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMEvernoteHandler;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.MulStatusListener;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.controller.media.EvernoteShareContent;
import com.umeng.socialize.facebook.controller.UMFacebookHandler;
import com.umeng.socialize.facebook.media.FaceBookShareContent;
import com.umeng.socialize.flickr.controller.UMFlickrHandler;
import com.umeng.socialize.flickr.media.FlickrShareContent;
import com.umeng.socialize.instagram.controller.UMInstagramHandler;
import com.umeng.socialize.instagram.media.InstagramShareContent;
import com.umeng.socialize.kakao.controller.UMKakaoHandler;
import com.umeng.socialize.kakao.media.KakaoShareContent;
import com.umeng.socialize.laiwang.controller.UMLWHandler;
import com.umeng.socialize.laiwang.media.LWDynamicShareContent;
import com.umeng.socialize.laiwang.media.LWShareContent;
import com.umeng.socialize.line.controller.UMLineHandler;
import com.umeng.socialize.line.media.LineShareContent;
import com.umeng.socialize.linkedin.controller.UMLinkedInHandler;
import com.umeng.socialize.linkedin.media.LinkedInShareContent;
import com.umeng.socialize.media.GooglePlusShareContent;
import com.umeng.socialize.media.MailShareContent;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.RenrenShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.SmsShareContent;
import com.umeng.socialize.media.TencentWbShareContent;
import com.umeng.socialize.media.TwitterShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMusic;
import com.umeng.socialize.pinterest.controller.UMPinterestHandler;
import com.umeng.socialize.pinterest.media.PinterestShareContent;
import com.umeng.socialize.pocket.controller.UMPocketHandler;
import com.umeng.socialize.pocket.media.PocketShareContent;
import com.umeng.socialize.sso.EmailHandler;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.RenrenSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.SmsHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.tumblr.controller.UMTumblrHandler;
import com.umeng.socialize.tumblr.media.TumblrShareContent;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.umeng.socialize.whatsapp.controller.UMWhatsAppHandler;
import com.umeng.socialize.whatsapp.media.WhatsAppShareContent;
import com.umeng.socialize.yixin.controller.UMYXHandler;
import com.umeng.socialize.ynote.controller.UMYNoteHandler;
import com.umeng.socialize.ynote.media.YNoteShareContent;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

public class ShareUtil {
    private static UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
    private static SHARE_MEDIA mPlatform = SHARE_MEDIA.SINA;
    static Activity context;
	public static void open(Activity context, String mode){
		//在不考虑是否授权的情况下区别openShare、postShare、directShare三个方法. openShare : 分享面板 -> 分享编辑页 -> 分享；postShare : 分享编辑页 -> 分享；directShare : 分享
		configPlatforms(); // 配置需要分享的相关平台
		if ("common".equals(mode)) {
            mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                    SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.SINA, SHARE_MEDIA.TENCENT,
                    SHARE_MEDIA.DOUBAN,
                    SHARE_MEDIA.RENREN);
            mController.openShare(context, false);
        } else if ("post_share".equals(mode)) {
            postShare();
        } else if ("direct_share".equals(mode)) {
            directShare();
        } else if ("share_multi".equals(mode)) {
            shareMult();
        } else if ("all".equals(mode)) {
            addCustomPlatforms();
        }
        setShareContent(); // 设置分享的内容
	}

	
    /**
     * 配置分享平台参数
     */
    private static void configPlatforms() {
        // 添加新浪SSO授权
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        // 添加腾讯微博SSO授权
        mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
        // 添加人人网SSO授权
        RenrenSsoHandler renrenSsoHandler = new RenrenSsoHandler(context,
                "201874", "28401c0964f04a72a14c812d6132fcef",
                "3bf66e42db1e4fa9829b955cc300b737");
        mController.getConfig().setSsoHandler(renrenSsoHandler);
        // 添加QQ、QZone平台
        addQQQZonePlatform();
        // 添加微信、微信朋友圈平台
        addWXPlatform();
    }
    
    /**
     * 根据不同的平台设置不同的分享内容</br>
     */
    private static void setShareContent() {

        // 配置SSO
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        mController.getConfig().setSsoHandler(new TencentWBSsoHandler());

        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(context,
                "100424468", "c7394704798a158208a74ab60104f0ba");
        qZoneSsoHandler.addToSocialSDK();
        mController.setShareContent("友盟社会化组件（SDK）让移动应用快速整合社交分享功能。http://www.umeng.com/social");

        // APP ID：201874, API
        // * KEY：28401c0964f04a72a14c812d6132fcef, Secret
        // * Key：3bf66e42db1e4fa9829b955cc300b737.
        RenrenSsoHandler renrenSsoHandler = new RenrenSsoHandler(context,
                "201874", "28401c0964f04a72a14c812d6132fcef",
                "3bf66e42db1e4fa9829b955cc300b737");
        mController.getConfig().setSsoHandler(renrenSsoHandler);

        UMImage localImage = new UMImage(context, R.drawable.device);
        UMImage urlImage = new UMImage(context,
                "http://www.umeng.com/images/pic/social/integrated_3.png");
        // UMImage resImage = new UMImage(context, R.drawable.ic_launcher);

        // 视频分享
        UMVideo video = new UMVideo(
                "http://v.youku.com/v_show/id_XNTc0ODM4OTM2.html");
        // vedio.setThumb("http://www.umeng.com/images/pic/home/social/img-1.png");
        video.setTitle("友盟社会化组件视频");
        video.setThumb(urlImage);

        UMusic uMusic = new UMusic(
                "http://music.huoxing.com/upload/20130330/1364651263157_1085.mp3");
        uMusic.setAuthor("umeng");
        uMusic.setTitle("天籁之音");
        // uMusic.setThumb(urlImage);
        uMusic.setThumb("http://www.umeng.com/images/pic/social/chart_1.png");

        // UMEmoji emoji = new UMEmoji(context,
        // "http://www.pc6.com/uploadimages/2010214917283624.gif");
        // UMEmoji emoji = new UMEmoji(context,
        // "/storage/sdcard0/emoji.gif");

        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent
                .setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-微信。http://www.umeng.com/social");
        weixinContent.setTitle("友盟社会化分享组件-微信");
        weixinContent.setTargetUrl("http://www.umeng.com/social");
        weixinContent.setShareMedia(urlImage);
        mController.setShareMedia(weixinContent);

        // 设置朋友圈分享的内容
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia
                .setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-朋友圈。http://www.umeng.com/social");
        circleMedia.setTitle("友盟社会化分享组件-朋友圈");
        circleMedia.setShareMedia(urlImage);
        // circleMedia.setShareMedia(uMusic);
        // circleMedia.setShareMedia(video);
        circleMedia.setTargetUrl("http://www.umeng.com/social");
        mController.setShareMedia(circleMedia);

        // 设置renren分享内容
        RenrenShareContent renrenShareContent = new RenrenShareContent();
        renrenShareContent.setShareContent("人人分享内容");
        UMImage image = new UMImage(context,
                BitmapFactory.decodeResource(context.getResources(), R.drawable.device));
        image.setTitle("thumb title");
        image.setThumb("http://www.umeng.com/images/pic/social/integrated_3.png");
        renrenShareContent.setShareImage(image);
        renrenShareContent.setAppWebSite("http://www.umeng.com/social");
        mController.setShareMedia(renrenShareContent);

        UMImage qzoneImage = new UMImage(context,
                "http://www.umeng.com/images/pic/social/integrated_3.png");
        qzoneImage
                .setTargetUrl("http://www.umeng.com/images/pic/social/integrated_3.png");

        // 设置QQ空间分享内容
        QZoneShareContent qzone = new QZoneShareContent();
        qzone.setShareContent("share test");
        qzone.setTargetUrl("http://www.umeng.com");
        qzone.setTitle("QZone title");
        qzone.setShareMedia(urlImage);
        // qzone.setShareMedia(uMusic);
        mController.setShareMedia(qzone);

        video.setThumb(new UMImage(context, BitmapFactory.decodeResource(
        		context.getResources(), R.drawable.device)));

        QQShareContent qqShareContent = new QQShareContent();
        qqShareContent.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能 -- QQ");
        qqShareContent.setTitle("hello, title");
        qqShareContent.setShareMedia(image);
        qqShareContent.setTargetUrl("http://www.umeng.com/social");
        mController.setShareMedia(qqShareContent);

        // 视频分享
        UMVideo umVideo = new UMVideo(
                "http://v.youku.com/v_show/id_XNTc0ODM4OTM2.html");
        umVideo.setThumb("http://www.umeng.com/images/pic/home/social/img-1.png");
        umVideo.setTitle("友盟社会化组件视频");

        TencentWbShareContent tencent = new TencentWbShareContent();
        tencent.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-腾讯微博。http://www.umeng.com/social");
        // 设置tencent分享内容
        mController.setShareMedia(tencent);

        // 设置邮件分享内容， 如果需要分享图片则只支持本地图片
        MailShareContent mail = new MailShareContent(localImage);
        mail.setTitle("share form umeng social sdk");
        mail.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-email。http://www.umeng.com/social");
        // 设置tencent分享内容
        mController.setShareMedia(mail);

        // 设置短信分享内容
        SmsShareContent sms = new SmsShareContent();
        sms.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-短信。http://www.umeng.com/social");
        // sms.setShareImage(urlImage);
        mController.setShareMedia(sms);

        SinaShareContent sinaContent = new SinaShareContent();
        sinaContent
                .setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-新浪微博。http://www.umeng.com/social");
        mController.setShareMedia(sinaContent);

        TwitterShareContent twitterShareContent = new TwitterShareContent();
        twitterShareContent
                .setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-TWITTER。http://www.umeng.com/social");
        twitterShareContent.setShareMedia(new UMImage(context, new File(
                "/storage/sdcard0/emoji.gif")));
        mController.setShareMedia(twitterShareContent);

        GooglePlusShareContent googlePlusShareContent = new GooglePlusShareContent();
        googlePlusShareContent
                .setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-G+。http://www.umeng.com/social");
        googlePlusShareContent.setShareMedia(localImage);
        mController.setShareMedia(googlePlusShareContent);

        // 来往分享内容
        LWShareContent lwShareContent = new LWShareContent();
        // lwShareContent.setShareImage(urlImage);
        // lwShareContent.setShareMedia(uMusic);
        lwShareContent.setShareMedia(umVideo);
        lwShareContent.setTitle("友盟社会化分享组件-来往");
        lwShareContent.setMessageFrom("友盟分享组件");
        lwShareContent
                .setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-来往。http://www.umeng.com/social");
        mController.setShareMedia(lwShareContent);

        // 来往动态分享内容
        LWDynamicShareContent lwDynamicShareContent = new LWDynamicShareContent();
        // lwDynamicShareContent.setShareImage(urlImage);
        // lwDynamicShareContent.setShareMedia(uMusic);
        lwDynamicShareContent.setShareMedia(umVideo);
        lwDynamicShareContent.setTitle("友盟社会化分享组件-来往动态");
        lwDynamicShareContent.setMessageFrom("来自友盟");
        lwDynamicShareContent
                .setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-来往动态。http://www.umeng.com/social");
        lwDynamicShareContent.setTargetUrl("http://www.umeng.com/social");
        mController.setShareMedia(lwDynamicShareContent);
    }
    

    /**
     * 调用postShare分享。跳转至分享编辑页，然后再分享。
     * [注意]对于新浪，豆瓣，人人，腾讯微博跳转到分享编辑页，其他平台直接跳转到对应的客户端
     */
    private static void postShare() {
        /*CustomShareBoard shareBoard = new CustomShareBoard(context);
        shareBoard.showAtLocation(context.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);*/
    }

    /**
     * 直接分享，底层分享接口。如果分享的平台是新浪、腾讯微博、豆瓣、人人，则直接分享，无任何界面弹出； 其它平台分别启动客户端分享</br>
     */
    private static void directShare() {
        mController.directShare(context, mPlatform, new SnsPostListener() {

            @Override
            public void onStart() {
            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
                String showText = "分享成功";
                if (eCode != StatusCode.ST_CODE_SUCCESSED) {
                    showText = "分享失败 [" + eCode + "]";
                }
                Toast.makeText(context, showText, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 一键分享到多个已授权平台。</br>
     */
    private static void shareMult() {
        SHARE_MEDIA[] platforms = new SHARE_MEDIA[] {
                SHARE_MEDIA.SINA, SHARE_MEDIA.TENCENT, SHARE_MEDIA.DOUBAN, SHARE_MEDIA.RENREN
        };
        mController.postShareMulti(context, new MulStatusListener() {

            @Override
            public void onStart() {
            }

            @Override
            public void onComplete(MultiStatus multiStatus, int st, SocializeEntity entity) {
                String showText = "分享结果：" + multiStatus.toString();
                Toast.makeText(context, showText, Toast.LENGTH_SHORT).show();
            }
        }, platforms);
    }



    /**
     * 添加所有的平台</br>
     */
    private static void addCustomPlatforms() {
        // 添加微信平台
        addWXPlatform();
        // 添加QQ平台
        addQQQZonePlatform();
        // 添加印象笔记平台
        addEverNote();
        // 添加facebook平台
        addFacebook();
        // 添加Instagram平台
        addInstagram();
        // 添加来往、来往动态平台
        addLaiWang();
        // 添加LinkedIn平台
        addLinkedIn();
        // 添加Pinterest平台
        addPinterest();
        // 添加Pocket平台
        addPocket();
        // 添加有道云平台
        addYNote();
        // 添加易信平台
        addYXPlatform();
        // 添加短信平台
        addSMS();
        // 添加email平台
        addEmail();

        addWhatsApp();
        addLine();
        addTumblr();
        addkakao();
        addFlickr();

        mController.registerListener(new SnsPostListener() {

            @Override
            public void onStart() {
                Toast.makeText(context, "share start...", 0).show();
            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
                Toast.makeText(context, "code : " + eCode, 0).show();
            }
        });

        mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.SINA, SHARE_MEDIA.TENCENT,
                SHARE_MEDIA.DOUBAN,
                SHARE_MEDIA.RENREN, SHARE_MEDIA.EMAIL, SHARE_MEDIA.EVERNOTE, SHARE_MEDIA.FACEBOOK,
                SHARE_MEDIA.GOOGLEPLUS, SHARE_MEDIA.INSTAGRAM, SHARE_MEDIA.LAIWANG,
                SHARE_MEDIA.LAIWANG_DYNAMIC, SHARE_MEDIA.LINKEDIN, SHARE_MEDIA.PINTEREST,
                SHARE_MEDIA.POCKET, SHARE_MEDIA.SMS, SHARE_MEDIA.TWITTER, SHARE_MEDIA.YIXIN,
                SHARE_MEDIA.YIXIN_CIRCLE, SHARE_MEDIA.YNOTE, SHARE_MEDIA.WHATSAPP,
                SHARE_MEDIA.LINE, SHARE_MEDIA.TUMBLR, SHARE_MEDIA.FLICKR, SHARE_MEDIA.KAKAO);
        mController.openShare(context, false);
    }

    /**
     * 添加短信平台</br>
     */
    private static void addSMS() {
        // 添加短信
        SmsHandler smsHandler = new SmsHandler();
        smsHandler.addToSocialSDK();
    }

    /**
     * 添加Email平台</br>
     */
    private static void addEmail() {
        // 添加email
        EmailHandler emailHandler = new EmailHandler();
        emailHandler.addToSocialSDK();
    }

    /**
     * Pocket分享。pockect只支持分享网络链接</br>
     */
    private static void addPocket() {
        UMPocketHandler pocketHandler = new UMPocketHandler(context);
        pocketHandler.addToSocialSDK();
        PocketShareContent pocketShareContent = new PocketShareContent();
        pocketShareContent.setShareContent("http://www.umeng.com/social");
        mController.setShareMedia(pocketShareContent);
    }

    /**
     * LinkedIn分享。LinkedIn只支持图片，文本，图文分享</br>
     */
    private static void addLinkedIn() {
        UMLinkedInHandler linkedInHandler = new UMLinkedInHandler(context);
        linkedInHandler.addToSocialSDK();
        LinkedInShareContent linkedInShareContent = new LinkedInShareContent();
        linkedInShareContent
                .setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-LinkedIn。http://www.umeng.com/social");
        mController.setShareMedia(linkedInShareContent);
    }

    /**
     * 有道云笔记分享。有道云笔记只支持图片，文本，图文分享</br>
     */
    private static void addYNote() {
        UMYNoteHandler yNoteHandler = new UMYNoteHandler(context);
        yNoteHandler.addToSocialSDK();
        YNoteShareContent yNoteShareContent = new YNoteShareContent();
        yNoteShareContent
                .setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-云有道笔记。http://www.umeng.com/social");
        yNoteShareContent.setTitle("友盟分享组件");
        yNoteShareContent.setShareImage(new UMImage(context, R.drawable.ic_launcher));
        mController.setShareMedia(yNoteShareContent);
    }

    /**
     * 添加印象笔记平台
     */
    private static void addEverNote() {
        UMEvernoteHandler evernoteHandler = new UMEvernoteHandler(context);
        evernoteHandler.addToSocialSDK();

        // 设置evernote的分享内容
        EvernoteShareContent shareContent = new EvernoteShareContent(
                "来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-EverNote。http://www.umeng.com/social");
        shareContent.setShareMedia(new UMImage(context, R.drawable.test));
        mController.setShareMedia(shareContent);
    }

    /**
     * 添加Pinterest平台
     */
    private static void addPinterest() {
        /**
         * app id需到pinterest开发网站( https://developers.pinterest.com/ )自行申请.
         */
        UMPinterestHandler pinterestHandler = new UMPinterestHandler(
                context, "1439206");
        pinterestHandler.addToSocialSDK();

        // 设置Pinterest的分享内容
        PinterestShareContent shareContent = new PinterestShareContent(
                "来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-Pinterest。http://www.umeng.com/social");
        shareContent.setShareMedia(new UMImage(context, R.drawable.test));
        mController.setShareMedia(shareContent);
    }

    /**
     * 添加来往和来往动态平台</br>
     */
    private static void addLaiWang() {

        String appToken = "laiwangd497e70d4";
        String secretID = "d497e70d4c3e4efeab1381476bac4c5e";
        // laiwangd497e70d4:来往appToken,d497e70d4c3e4efeab1381476bac4c5e:来往secretID
        // 添加来往的支持
        UMLWHandler umlwHandler = new UMLWHandler(context, appToken, secretID);
        umlwHandler.addToSocialSDK();

        // 添加来往动态的支持
        UMLWHandler lwDynamicHandler = new UMLWHandler(context,
                appToken, secretID);
        lwDynamicHandler.setToCircle(true);
        lwDynamicHandler.addToSocialSDK();
    }

    /**
     * @功能描述 : 添加微信平台分享
     * @return
     */
    private static void addWXPlatform() {
        // 注意：在微信授权的时候，必须传递appSecret
        // wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
        String appId = "wx967daebe835fbeac";
        String appSecret = "5bb696d9ccd75a38c8a0bfe0675559b3";
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(context, appId, appSecret);
        wxHandler.addToSocialSDK();

        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(context, appId, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }

    /**
     * @功能描述 : 添加QQ平台支持 QQ分享的内容， 包含四种类型， 即单纯的文字、图片、音乐、视频. 参数说明 : title, summary,
     *       image url中必须至少设置一个, targetUrl必须设置,网页地址必须以"http://"开头 . title :
     *       要分享标题 summary : 要分享的文字概述 image url : 图片地址 [以上三个参数至少填写一个] targetUrl
     *       : 用户点击该分享时跳转到的目标地址 [必填] ( 若不填写则默认设置为友盟主页 )
     * @return
     */
    private static void addQQQZonePlatform() {
        String appId = "100424468";
        String appKey = "c7394704798a158208a74ab60104f0ba";
        // 添加QQ支持, 并且设置QQ分享内容的target url
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(context,
                appId, appKey);
        qqSsoHandler.setTargetUrl("http://www.umeng.com/social");
        qqSsoHandler.addToSocialSDK();

        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(context, appId, appKey);
        qZoneSsoHandler.addToSocialSDK();
    }

    /**
     * @Title: addYXPlatform
     * @Description:
     * @throws
     */
    private static void addYXPlatform() {

        // 添加易信平台
        UMYXHandler yixinHandler = new UMYXHandler(context,
                "yxc0614e80c9304c11b0391514d09f13bf");
        // 关闭分享时的等待Dialog
        yixinHandler.enableLoadingDialog(false);
        // 设置target Url, 必须以http或者https开头
        yixinHandler.setTargetUrl("http://www.umeng.com/social");
        yixinHandler.addToSocialSDK();

        // 易信朋友圈平台
        UMYXHandler yxCircleHandler = new UMYXHandler(context,
                "yxc0614e80c9304c11b0391514d09f13bf");
        yxCircleHandler.setToCircle(true);
        yxCircleHandler.addToSocialSDK();

    }

    /**
     * @Title: addFacebook
     * @Description:
     * @throws
     */
    private static void addFacebook() {
        UMFacebookHandler mFacebookHandler = new UMFacebookHandler(context);
        mFacebookHandler.addToSocialSDK();
        UMImage localImage = new UMImage(context, R.drawable.device);
        UMVideo umVedio = new UMVideo(
                "http://v.youku.com/v_show/id_XNTc0ODM4OTM2.html");
        umVedio.setThumb("http://www.umeng.com/images/pic/banner_module_social.png");
        umVedio.setTitle("友盟社会化组件视频");
        FaceBookShareContent fbContent = new FaceBookShareContent();
//         fbContent.setShareImage(new UMImage(context,
//         "http://www.umeng.com/images/pic/social/integrated_3.png"));
//        fbContent.setShareImage(localImage);
//         fbContent.setShareContent("This is my facebook social share sdk.");
//        fbContent.setShareVideo(umVedio);
        fbContent.setTitle("FB title");
        fbContent.setCaption("Caption - Fb");
        fbContent.setShareContent("友盟分享组件支持FB最新版啦~");
        fbContent.setTargetUrl("http://www.umeng.com/social");
        mController.setShareMedia(fbContent);
    }

    /**
     * Instagram只支持图片分享, 只支持纯图片分享.
     */
    private static void addInstagram() {
        // 构建Instagram的Handler
        UMInstagramHandler instagramHandler = new UMInstagramHandler(
                context);
        instagramHandler.addToSocialSDK();
        UMImage localImage = new UMImage(context, R.drawable.device);
        // // 添加分享到Instagram的内容
        InstagramShareContent instagramShareContent = new InstagramShareContent(
                localImage);
        mController.setShareMedia(instagramShareContent);
    }

    private static void addWhatsApp() {
        UMWhatsAppHandler whatsAppHandler = new UMWhatsAppHandler(context);
        whatsAppHandler.addToSocialSDK();
        WhatsAppShareContent whatsAppShareContent = new WhatsAppShareContent();
        // whatsAppShareContent.setShareContent("share test");
        whatsAppShareContent.setShareImage(new UMImage(context, R.drawable.ic_launcher));
        mController.setShareMedia(whatsAppShareContent);
        // mController.openShare(context, false);
    }

    private static void addLine() {
        UMLineHandler lineHandler = new UMLineHandler(context);
        lineHandler.addToSocialSDK();
        LineShareContent lineShareContent = new LineShareContent();
        // lineShareContent.setShareContent("share test");
        lineShareContent.setShareImage(new UMImage(context, R.drawable.ic_launcher));
        mController.setShareMedia(lineShareContent);
        // mController.openShare(context, false);
    }

    private static void addTumblr() {
        UMTumblrHandler tumblrHandler = new UMTumblrHandler(context);
        tumblrHandler.addToSocialSDK();
        TumblrShareContent tumblrShareContent = new TumblrShareContent();
        tumblrShareContent.setTitle("title");
        tumblrShareContent.setShareContent("share test");
        tumblrShareContent.setShareImage(new UMImage(context, R.drawable.ic_launcher));
        mController.setShareMedia(tumblrShareContent);
        // mController.openShare(context, false);
    }

    private static void addkakao() {
        UMKakaoHandler kakaoHandler = new UMKakaoHandler(context);
        kakaoHandler.addToSocialSDK();
        KakaoShareContent kakaoShareContent = new KakaoShareContent();
        // kakaoShareContent.setShareContent("share test");
        kakaoShareContent.setShareImage(new UMImage(context, R.drawable.ic_launcher));
        mController.setShareMedia(kakaoShareContent);
        // mController.openShare(context, false);
    }

    private static void addFlickr() {
        UMFlickrHandler flickrHandler = new UMFlickrHandler(context);
        flickrHandler.addToSocialSDK();
        FlickrShareContent flickrShareContent = new FlickrShareContent();
        flickrShareContent.setShareImage(new UMImage(context, R.drawable.ic_launcher));
        // flickrShareContent.setShareContent("share test");
        mController.setShareMedia(flickrShareContent);
        // mController.openShare(context, false);
    }
}