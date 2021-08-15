package cn.egenie.architect.common.core.util;

import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.HibernateValidator;


/**
 * @author lucien
 * @since 2021/01/17
 */
public class ValidatorUtils {
    private static ValidatorFactory factory = Validation.byProvider(HibernateValidator.class)
            .configure()
            .buildValidatorFactory();

    private static Validator validator = factory.getValidator();


    /**
     * 手机号正则
     */
    private static Pattern phoneNoPattern = Pattern.compile("^1\\d{10}$");

    /**
     * 邮箱正则
     */
    private static Pattern emailPattern = Pattern.compile("^([a-z0-9A-Z]+[-|\\.|_]?)*[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");

    public static <T> void validate(T target, Class<?>... groups) {
        String validateResult = getValidateResult(target, groups);
        Assert.throwIfTrue(StringUtils.isNotBlank(validateResult), validateResult);
    }


    public static <T> String getValidateResult(T target, Class<?>... groups) {
        Set<ConstraintViolation<T>> violationSet = validator.validate(target, groups);
        if (CollectionUtils.isEmpty(violationSet)) {
            return "";
        }

        StringBuilder message = new StringBuilder();
        violationSet.forEach(violation -> message.append(violation.getMessage()).append(" ,"));

        return message.substring(0, message.length() - 1);
    }

    public static boolean isPhone(String phone) {
        return StringUtils.isNotBlank(phone)
                && phoneNoPattern.matcher(phone).matches();
    }

    public static boolean isEmail(String email) {
        return StringUtils.isNotBlank(email)
                && emailPattern.matcher(email).matches();
    }

    public static boolean isFuture(Date date) {
        return Objects.nonNull(date)
                && date.toInstant().isAfter(TimeUtils.now().toInstant());
    }
}
