package com.angularjsplay.mvc;

import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mangofactory.swagger.configuration.JacksonScalaSupport;
import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.mangofactory.swagger.core.SwaggerApiResourceListing;
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider;
import com.mangofactory.swagger.scanners.ApiListingReferenceScanner;
import com.wordnik.swagger.model.ApiInfo;

@Configuration
public class ScrumSwaggerConfig {

	public static final String SWAGGER_GROUP = "";

	public static final List<String> DEFAULT_INCLUDE_PATTERNS = Arrays
			.asList(new String[] { "/users.*", "/projects.*",
					"/sprints.*", "/backlogs.*", "/tasks.*" });

	@Autowired
	private SpringSwaggerConfig springSwaggerConfig;

	/**
	 * Adds the jackson scala module to the MappingJackson2HttpMessageConverter
	 * registered with spring Swagger core models are scala so we need to be
	 * able to convert to JSON Also registers some custom serializers needed to
	 * transform swagger models to swagger-ui required json format
	 */
	@Bean
	public JacksonScalaSupport jacksonScalaSupport() {
		JacksonScalaSupport jacksonScalaSupport = new JacksonScalaSupport();
		// Set to false to disable
		jacksonScalaSupport.setRegisterScalaModule(true);
		return jacksonScalaSupport;
	}

	@Bean
	// Completely optional! if you've already got an object mapper it will
	// automatically
	// use the bean that is customized
	public ObjectMapper customObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		// Additional customizations go here
		return objectMapper;
	}

	@Bean
	public SwaggerGlobalSettings swaggerGlobalSettings() {
		SwaggerGlobalSettings swaggerGlobalSettings = new SwaggerGlobalSettings();
		swaggerGlobalSettings.setGlobalResponseMessages(springSwaggerConfig
				.defaultResponseMessages());
		swaggerGlobalSettings.setIgnorableParameterTypes(springSwaggerConfig
				.defaultIgnorableParameterTypes());
		AlternateTypeProvider alternateTypeProvider = springSwaggerConfig
				.defaultAlternateTypeProvider();
		swaggerGlobalSettings.setAlternateTypeProvider(alternateTypeProvider);
		return swaggerGlobalSettings;
	}

	/**
	 * API Info as it appears on the swagger-ui page
	 */
	private ApiInfo apiInfo() {
		ApiInfo apiInfo = new ApiInfo("Demo Spring MVC swagger 1.2 api",
				"Sample spring mvc api based on the swagger 1.2 spec",
				"http://en.wikipedia.org/wiki/Terms_of_service",
				"somecontact@somewhere.com", "Apache 2.0",
				"http://www.apache.org/licenses/LICENSE-2.0.html");
		return apiInfo;
	}

	/**
	 * Configure a SwaggerApiResourceListing for each swagger instance within
	 * your app. e.g. 1. private 2. external apis Required to be a spring bean
	 * as spring will call the postConstruct method to bootstrap swagger
	 * scanning.
	 * 
	 * @return
	 */
	@Bean
	public SwaggerApiResourceListing swaggerApiResourceListing() {
		// The group name is important and should match the group set on
		// ApiListingReferenceScanner
		// Note that swaggerCache() is by DefaultSwaggerController to serve the
		// swagger json
		SwaggerApiResourceListing swaggerApiResourceListing = new SwaggerApiResourceListing(
				springSwaggerConfig.swaggerCache(), SWAGGER_GROUP);

		// Set the required swagger settings
		swaggerApiResourceListing
				.setSwaggerGlobalSettings(swaggerGlobalSettings());

		// Use a custom path provider or
		// springSwaggerConfig.defaultSwaggerPathProvider()
		swaggerApiResourceListing.setSwaggerPathProvider(springSwaggerConfig
				.defaultSwaggerPathProvider());

		// Supply the API Info as it should appear on swagger-ui web page
		swaggerApiResourceListing.setApiInfo(apiInfo());

		// Global authorization - see the swagger documentation
		// swaggerApiResourceListing.setAuthorizationTypes(authorizationTypes());

		// Sets up an auth context - i.e. which controller request paths to
		// apply global auth to
		// swaggerApiResourceListing
		// .setAuthorizationContext(authorizationContext());

		// Every SwaggerApiResourceListing needs an ApiListingReferenceScanner
		// to scan the spring request mappings
		swaggerApiResourceListing
				.setApiListingReferenceScanner(apiListingReferenceScanner());

		// Set the model provider, uses the default autowired model provider.
		// swaggerApiResourceListing.setModelProvider(modelProvider);

		return swaggerApiResourceListing;
	}

	@Bean
	/**
	 * The ApiListingReferenceScanner does most of the work.
	 * Scans the appropriate spring RequestMappingHandlerMappings
	 * Applies the correct absolute paths to the generated swagger resources
	 */
	public ApiListingReferenceScanner apiListingReferenceScanner() {
		ApiListingReferenceScanner apiListingReferenceScanner = new ApiListingReferenceScanner();

		// Picks up all of the registered spring RequestMappingHandlerMappings
		// for scanning
		apiListingReferenceScanner
				.setRequestMappingHandlerMapping(springSwaggerConfig
						.swaggerRequestMappingHandlerMappings());

		// Excludes any controllers with the supplied annotations
		apiListingReferenceScanner.setExcludeAnnotations(springSwaggerConfig
				.defaultExcludeAnnotations());

		//
		apiListingReferenceScanner
				.setResourceGroupingStrategy(springSwaggerConfig
						.defaultResourceGroupingStrategy());

		// Path provider used to generate the appropriate uri's
		apiListingReferenceScanner.setSwaggerPathProvider(springSwaggerConfig
				.defaultSwaggerPathProvider());

		// Must match the swagger group set on the SwaggerApiResourceListing
		apiListingReferenceScanner.setSwaggerGroup(SWAGGER_GROUP);

		// Only include paths that match the supplied regular expressions
		apiListingReferenceScanner.setIncludePatterns(DEFAULT_INCLUDE_PATTERNS);

		return apiListingReferenceScanner;
	}

	// private List<AuthorizationType> authorizationTypes() {
	// ArrayList<AuthorizationType> authorizationTypes = new
	// ArrayList<AuthorizationType>();

	// List<AuthorizationScope> authorizationScopeList = newArrayList();
	// authorizationScopeList.add(new AuthorizationScope("global",
	// "access all"));
	//
	//
	// List<GrantType> grantTypes = newArrayList();
	//
	// LoginEndpoint loginEndpoint = new
	// LoginEndpoint("http://petstore.swagger.wordnik.com/oauth/dialog");
	// grantTypes.add(new ImplicitGrant(loginEndpoint, "access_token"));
	//
	// TokenRequestEndpoint tokenRequestEndpoint = new
	// TokenRequestEndpoint("http://petstore.swagger.wordnik.com/oauth/requestToken",
	// "client_id", "client_secret");
	// TokenEndpoint tokenEndpoint = new
	// TokenEndpoint("http://petstore.swagger.wordnik.com/oauth/token",
	// "auth_code");
	//
	// AuthorizationCodeGrant authorizationCodeGrant = new
	// AuthorizationCodeGrant(tokenRequestEndpoint, tokenEndpoint);
	// grantTypes.add(authorizationCodeGrant);
	//
	// OAuth oAuth = new OAuthBuilder()
	// .scopes(authorizationScopeList)
	// .grantTypes(grantTypes)
	// .build();
	//
	// authorizationTypes.add(oAuth);
	// return authorizationTypes;
	// }

	// @Bean
	// public AuthorizationContext authorizationContext() {
	// List<Authorization> authorizations = new ArrayList<Authorization>();

	// AuthorizationScope authorizationScope = new AuthorizationScope(
	// "global", "accessEverything");
	// AuthorizationScope[] authorizationScopes = new AuthorizationScope[] {
	// authorizationScope };
	// authorizations.add(new Authorization("oauth2", authorizationScopes));
	// AuthorizationContext authorizationContext = new
	// AuthorizationContext.AuthorizationContextBuilder(
	// authorizations).withIncludePatterns(DEFAULT_INCLUDE_PATTERNS)
	// .build();
	// return authorizationContext;
	// }

}
