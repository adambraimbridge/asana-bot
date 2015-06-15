package com.ft.report;

import lombok.Setter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Controller
@RequestMapping("/reports")
public class ReportsController {

    private static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");



    @Setter @Resource(name = "reportGenerators")
    private Map<ReportType, ReportGenerator> reportGenerators;

    @Setter private Clock clock = Clock.systemDefaultZone();

    @ModelAttribute("reportTypes")
    public ReportType[] populateReportTypes() {
        return ReportType.values();
    }


    @ModelAttribute("preferredReportType")
    public ReportType populatePreferredReportType() {
        LocalDateTime now = LocalDateTime.now(clock);
        if (now.getHour() < 11) {
            return ReportType.MORNING;
        } else if (now.getDayOfWeek().equals(DayOfWeek.FRIDAY)) {
            return ReportType.SUNDAY_FOR_MONDAY;
        }

        return ReportType.EVENING;
    }

    @ModelAttribute("teams")
    public List populateUserTeams() {
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        Map authDetails = (Map) oAuth2Authentication.getUserAuthentication().getDetails();
        return (List) authDetails.get("teams");
    }



    @RequestMapping(method = RequestMethod.GET)
    public String home(@ModelAttribute("teams") List teams,
                       @ModelAttribute("preferredReportType") ReportType preferredReportType,
                       Map<String, Object> model) {
        Criteria criteria = new Criteria();
        if (!teams.isEmpty()) {
            criteria.setTeam((String) teams.get(0));
        }
        criteria.setReportType(preferredReportType);

        model.put("criteria", criteria);
        return "reports/home";
    }
    @RequestMapping(method = RequestMethod.POST)
    public String create(@ModelAttribute Criteria criteria, ModelMap modelMap) {
        modelMap.addAttribute("criteria", criteria);

        Report report = reportGenerators.get(criteria.getReportType()).generate(criteria.getTeam());
        modelMap.addAttribute("report", report);
        modelMap.addAttribute("reportDate", buildReportDate(criteria.getReportType()));
        return "reports/home";
    }

    public String buildReportDate(ReportType preferredReportType) {
        LocalDate today = LocalDate.now(clock);

        if (preferredReportType == ReportType.SUNDAY_FOR_MONDAY) {
            LocalDate sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            LocalDate monday = sunday.plusDays(1);
            return sunday.format(dateFormat) + " - " + monday.format(dateFormat);
        }

        return today.format(dateFormat);
    }

}
