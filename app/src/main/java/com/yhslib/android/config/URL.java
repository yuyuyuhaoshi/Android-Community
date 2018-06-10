package com.yhslib.android.config;

public class URL {
    // 文档 https://www.showdoc.cc/web/#/djangochina
    public static final String host = "http://api.dj-china.org";

    public static class User {
        public static String registration() {
            // method:post
            // return http://api.dj-china.org/rest-auth/registration/
            return host + "/rest-auth/registration/";
        }

        public static String login() {
            // method:post
            return host + "/rest-auth/login/";
        }

        public static String getEmailList() {
            // method:get
            // return http://api.dj-china.org/users/email/
            return host + "/users/email/";
        }

        public static String addEmail() {
            // method:post
            // return http://api.dj-china.org/users/email/
            return host + "/users/email/";
        }

        public static String detail(String userId) {
            // method:get
            // return http://api.dj-china.org/users/1/
            return host + "/users/" + userId + '/';
        }

        public static String changeNickname(String userId) {
            // method: patch
            // return http://api.dj-china.org/users/1/
            return host + "/users/" + userId + '/';
        }

        public static String getBalance(String userId) {
            // method: get
            // return http://api.dj-china.org/users/1/balance/
            return host + "/users/" + userId + "/balance/";
        }

        public static String getPosts(String userId) {
            // method: get
            // return http://api.dj-china.org/users/1/posts/
            return host + "/users/" + userId + "/posts/";
        }

        public static String deleteEmail(String userId) {
            // method: delete
            // return http://api.dj-china.org/users/1/posts/
            return host + "/users/email/" + userId + '/';
        }

        public static String reverifyEmail(String emailID) {
            // method: get
            // return http://api.dj-china.org/users/email/1/reverify/;
            return host + "/users/email/" + emailID + "/reverify/";
        }

        public static String setPrimaryEmail(String emailID) {
            // method: post
            // return http://api.dj-china.org/users/email/1/set_primary/;
            return host + "/users/email/" + emailID + "/set_primary/";
        }

        public static String changePassword() {
            // method: post
            // return http://api.dj-china.org/rest-auth/password/change/;
            return host + "/rest-auth/password/change/";
        }

        public static String checkin(String userId) {
            // method: post
            // return http://api.dj-china.org/users/1/checkin/;
            return host + "/users/" + userId + "/checkin/";
        }
    }

    public static class Post {
        public static String getPostDetail(String postID) {
            // method: get
            // return http://api.dj-china.org/post/1/;
            return host + "/posts/" + postID + "/";
        }

        public static String getPostReply(String postID) {
            // method: get
            // return http://api.dj-china.org/post/1/replies/;
            return host + "/posts/" + postID + "/replies/";
        }
    }

    public static class Community {
        public static String getPosts() {
            return host + "/posts/";
        }

        public static String getPopularTags() {
            return host + "/tags/popular/";
        }
    }

    public static class Notification {
        public static String getNotification() {
            return host + "/notifications/";
        }

        public static String deleteNotification(String id) {
            return host + "/notifications/" + id + "/";
        }
    }

    public static class Auth {
        public static String refreshJWTToken() {
            // method: post
            // return http://api.dj-china.org/rest-auth/jwt-refresh/;
            return host + "/rest-auth/jwt-refresh/";
        }


    }
}
