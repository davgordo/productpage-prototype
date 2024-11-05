/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat.bookinfo.productpage;

import java.util.ArrayList;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.model.rest.RestBindingMode;

@ApplicationScoped
public class ApiRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        restConfiguration().bindingMode(RestBindingMode.json)
                .bindingPackageScan("com.redhat.bookinfo.productpage");

        rest().openApi().specification("productpage-api.json").missingOperation("ignore");

        getContext().getTypeConverterRegistry().addTypeConverters(new ReviewsTypeConverter());

        from("direct:getProducts").process(e -> {

            Product product = new Product();
            product.setId(1);
            product.setTitle("Contract-First IDP");
            product.setDescriptionHtml("<b>really</b> great");

            ArrayList<Product> products = new ArrayList<Product>();
            products.add(product);

            e.getMessage().setBody(products);
        });

        from("direct:getProductReviews")
                .setVariable("productId", simple("${header.id}"))
                .removeHeaders("*")
                .setHeader("id", simple("${variable.productId}"))
                .to("rest-openapi:reviews-api.json#getProductReviews?host=http://localhost:9080&basePath=/")
                .unmarshal(new JacksonDataFormat(Reviews.class))
                .convertBodyTo(ProductReviews.class);

    }
}
