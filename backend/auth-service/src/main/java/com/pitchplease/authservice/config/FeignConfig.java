package com.pitchplease.authservice.config;
// package com.pitchplease.user.config;

// import feign.RequestInterceptor;
// import jakarta.servlet.http.HttpServletRequest;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.util.StringUtils;
// import org.springframework.web.context.request.RequestContextHolder;
// import org.springframework.web.context.request.ServletRequestAttributes;

// @Configuration
// public class FeignConfig {
//   @Bean
//   public RequestInterceptor bearerAuthInterceptor() {
//     return template -> {
//       ServletRequestAttributes attrs =
//           (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//       if (attrs != null) {
//         HttpServletRequest request = attrs.getRequest();
//         String auth = request.getHeader("Authorization");
//         if (StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
//           template.header("Authorization", auth);
//         }
//       }
//     };
//   }
// }
