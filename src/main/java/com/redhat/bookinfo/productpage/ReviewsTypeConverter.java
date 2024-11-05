package com.redhat.bookinfo.productpage;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Converter;

@ApplicationScoped
@Converter(generateLoader = true)
public class ReviewsTypeConverter {

    @Converter
    public static ProductReviews toProductReviews(Reviews reviews) {
        ProductReviews productReviews = new ProductReviews();
        productReviews.setId(Integer.parseInt(reviews.getId()));
        productReviews.setReviews(reviews.getReviews());
        return productReviews;
    }
}
