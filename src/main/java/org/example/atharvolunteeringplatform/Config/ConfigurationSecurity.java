package org.example.atharvolunteeringplatform.Config;

import lombok.RequiredArgsConstructor;
import org.example.atharvolunteeringplatform.Service.MyUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ConfigurationSecurity {


    private final MyUserDetailsService myUserDetailsService;

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(myUserDetailsService);
        authenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder());

        return authenticationProvider;
    }



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .authenticationProvider(daoAuthenticationProvider())
                .authorizeHttpRequests()

                // طالب
                .requestMatchers("/api/v1/student/add").permitAll()
                .requestMatchers(
                        "/api/v1/student/update",
                        "/api/v1/student/my-requests",
                        "/api/v1/student/opportunities-by-hours",
                        "/api/v1/student/opportunities-by-date/{from}/{to}",
                        "/api/v1/student/completed-opportunities",
                        "/api/v1/student/hours-summary"
                ).hasAuthority("student")

                // مشرف
                .requestMatchers("/api/v1/student/students-Inactive").hasAuthority("supervisor")

                // أدمن (من student controller)
                .requestMatchers(
                        "/api/v1/student/get-all",
                        "/api/v1/student/delete/{studentID}"
                ).hasAuthority("admin")

                // ================= SchoolController ==================
                // متاح للجميع
                .requestMatchers("/api/v1/school/add").permitAll()

                // أدمن فقط
                .requestMatchers(
                        "/api/v1/school/all",
                        "/api/v1/school/delete/{schoolId}",
                        "/api/v1/school/activate/{id}"
                ).hasAuthority("admin")

                // مشرف فقط
                .requestMatchers(
                        "/api/v1/school/update",
                        "/api/v1/school/volunteers/{grade}",
                        "/api/v1/school/students/non-volunteers/{gradeLevel}",
                        "/api/v1/school/student-opportunities/{studentId}",
                        "/api/v1/school/opportunity-request/{requestId}/complete",
                        "/api/v1/school/opportunity-request/{requestId}/incomplete",
                        "/api/v1/school/requests",
                        "/api/v1/school/accept/{requestId}",
                        "/api/v1/school/reject/{requestId}",
                        "/api/v1/school/students/{studentId}/active",
                        "/api/v1/school/notify-non-volunteering/{studentId}",
                        "/api/v1/school/students/reject/{studentId}",
                        "/api/v1/school/students/details/{studentId}"
                ).hasAuthority("supervisor")

                //  ========== StudentOpportunityRequestController ==========

                .requestMatchers("/api/v1/student-opportunity-request/get-all").hasAuthority("admin")
                .requestMatchers("/api/v1/student-opportunity-request/delete/{requestId}").hasAuthority("admin")

                .requestMatchers("/api/v1/student-opportunity-request/request/{opportunityId}").hasAuthority("student")
                .requestMatchers("/api/v1/student-opportunity-request/student/opportunities/filter/{status}").hasAuthority("student")

                .requestMatchers("/api/v1/student-opportunity-request/approve-request/{requestId}").hasAuthority("organization")
                .requestMatchers("/api/v1/student-opportunity-request/reject-request/{requestId}/").hasAuthority("organization")
                .requestMatchers("/api/v1/student-opportunity-request/Opportunity-Requests-By-Organization").hasAuthority("organization")


                // ================= ComplaintsController =================
                // متاح للطالب فقط
                .requestMatchers(
                        "/api/v1/Complaints/add/{opportunityId}",
                        "/api/v1/Complaints/update/{id}",
                        "/api/v1/Complaints/delete/{id}",
                        "/api/v1/Complaints/complaints",
                        "/api/v1/Complaints/by-date/{from}/{to}",
                        "/api/v1/Complaints/my-complaints/{status}",
                        "/api/v1/payments/card"
                ).hasAuthority("student")

                // متاح للأدمن فقط
                .requestMatchers(
                        "/api/v1/Complaints/all",
                        "/api/v1/Complaints/update-status/{id}"
                ).hasAuthority("admin")

                // ================== Organization endpoints ==================
                .requestMatchers("/api/v1/organization/add").permitAll()
                .requestMatchers(
                        "/api/v1/organization/update",
                        "/api/v1/organization/count/volunteers",
                        "/api/v1/organization/count/opportunities",
                        "/api/v1/organization/total-hours",
                        "/api/v1/organization/organization/pending",
                        "/api/v1/organization/reject-request/{requestId}",
                        "/api/v1/organization/accept-request/{requestId}",
                        "/api/v1/organization/organization/history",
                        "/api/v1/organization/open/{opportunityId}",
                        "/api/v1/organization/close/{opportunityId}",
                        "/api/v1/opportunity/get-Organization-Opportunities"
                ).hasAuthority("organization")

                // ================= Opportunity endpoints ==================
                .requestMatchers(
                        "/api/v1/opportunity/create",
                        "/api/v1/opportunity/update",
                        "/api/v1/opportunity/sorted-by-capacity",
                        "/api/v1/opportunity/count/open",
                        "/api/v1/opportunity/count/pending",
                        "/api/v1/opportunity/count/rejected",
                        "/api/v1/opportunity/count/closed",
                        "/api/v1/opportunity/count/total"
                ).hasAuthority("organization")

                .requestMatchers("/api/v1/get-Open-Opportunities").permitAll()

                // ================= Opportunity - admin ==================
                .requestMatchers(
                        "/api/v1/opportunity/get-all",
                        "/api/v1/opportunity/opportunities/image/{id}",
                        "/api/v1/opportunity/delete/{opportunityId}",
                        "/api/v1/opportunity/accept/{id}",
                        "/api/v1/opportunity/accept-edit/{id}",
                        "/api/v1/opportunity/reject/{id}",
                        "/api/v1/opportunity/reject-edit/{id}",
                        "/api/v1/opportunity/admin/change-opportunity-status/{opportunityId}/{newStatus}"
                ).hasAuthority("admin")

                // ================= Review endpoints ====================
                .requestMatchers(
                        "/api/v1/review/all",
                        "/api/v1/review/delete/{id}"
                ).hasAuthority("admin")

                // Supervisor: add review and delete own review
                .requestMatchers(
                        "/api/v1/review/add-review/opportunity/{opportunityId}",
                        "/api/v1/review/delete/{reviewId}"
                ).hasAuthority("supervisor")

                // Organization: view reviews, counts, and ratings
                .requestMatchers(
                        "/api/v1/review/get-review/{opportunityId}",
                        "/api/v1/review/organization/average-rating",
                        "/api/v1/review/organization/review-count",
                        "/api/v1/review/opportunity/{opportunityId}/average-rating",
                        "/api/v1/review/opportunity/{opportunityId}/review-count"
                ).hasAuthority("organization")

                .requestMatchers("/api/v1/badge/**").hasAuthority("admin")
                .anyRequest().authenticated()

                .and()
                .logout()
                .logoutUrl("/api/v1/logout")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .and()
                .httpBasic();

        return httpSecurity.build();
    }



}