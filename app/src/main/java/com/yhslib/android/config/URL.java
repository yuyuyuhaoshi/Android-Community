package com.yhslib.android.config;

public class URL {
    // 文档 https://www.showdoc.cc/web/#/djangochina
    public static final String host = "http://api.dj-china.org";

    public static class User {
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
    }
}
