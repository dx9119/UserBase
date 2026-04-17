package org.ukhanov.userbase.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.ukhanov.userbase.service.IpAddressService;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final IpAddressService ipAddressService;
    private final MessageSource messageSource;

    private static final DateTimeFormatter ERROR_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public GlobalExceptionHandler(IpAddressService ipAddressService, MessageSource messageSource) {
        this.ipAddressService = ipAddressService;
        this.messageSource = messageSource;
    }

    @ExceptionHandler(PasswordChangeException.class)
    public String handlePasswordChangeException(PasswordChangeException ex, Model model, HttpServletRequest request) {
        model.addAttribute("error", ex.getMessage());
        return "auth/change-password";
    }

    @ExceptionHandler(RegistrationException.class)
    public String handleRegistrationException(RegistrationException ex, Model model, HttpServletRequest request) {
        model.addAttribute("error", ex.getMessage());
        return "auth/register";
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleGeneralException(Exception ex, Model model, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex, model, request);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoResourceFoundException.class)
    public ModelAndView handle404(NoResourceFoundException ex, Model model, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex, model, request);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView handleIllegalArgument(IllegalArgumentException ex, Model model, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex, model, request);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RoleAlreadyExistsException.class)
    public ModelAndView handleRoleAlreadyExists(RoleAlreadyExistsException ex, Model model, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex, model, request);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EmptyReasonException.class)
    public ModelAndView handleEmptyReason(EmptyReasonException ex, Model model, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex, model, request);
    }

    private ModelAndView buildErrorResponse(HttpStatus status, Exception ex,
                                            Model model, HttpServletRequest request) {

        String clientIp = ipAddressService.getClientIpAddress(request);
        String safeMessage = getUserFriendlyMessage(ex, status);

        // Логирование
        if (status.is4xxClientError()) {
            logger.warn("Client error - IP: {}, URI: {}, Status: {}, Message: {}",
                    clientIp, request.getRequestURI(), status.value(), ex.getMessage());
        } else {
            logger.error("Server error - IP: {}, URI: {}, Status: {}",
                    clientIp, request.getRequestURI(), status.value(), ex);
        }

        // Добавляем в модель только безопасные данные
        model.addAttribute("errorTime", LocalDateTime.now().format(ERROR_TIME_FORMATTER));
        model.addAttribute("errorIp", clientIp);
        model.addAttribute("httpStatus", status.value());
        model.addAttribute("errorMessage", safeMessage);
        model.addAttribute("errorCode", status.getReasonPhrase());

        ModelAndView mav = new ModelAndView("error");
        mav.setStatus(status);
        return mav;
    }

    private String getUserFriendlyMessage(Exception ex, HttpStatus status) {
        Locale locale = LocaleContextHolder.getLocale();

        if (status == HttpStatus.BAD_REQUEST) {
            String badRequestMsg = messageSource.getMessage("error.badRequest", null, locale);
            return ex.getMessage() != null ? ex.getMessage() : badRequestMsg;
        }

        return switch (status) {
            case NOT_FOUND -> messageSource.getMessage("error.notFound", null, locale);
            case INTERNAL_SERVER_ERROR -> messageSource.getMessage("error.internal", null, locale);
            case FORBIDDEN -> messageSource.getMessage("error.forbidden", null, locale);
            case UNAUTHORIZED -> messageSource.getMessage("error.unauthorized", null, locale);
            default -> messageSource.getMessage("error.default", null, locale);
        };
    }
}