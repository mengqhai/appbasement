"// swagger-ui.js" 
"// version 2.1.0-alpha.7" 
$(function() {

	// Helper function for vertically aligning DOM elements
	// http://www.seodenver.com/simple-vertical-align-plugin-for-jquery/
	$.fn.vAlign = function() {
		return this.each(function(i){
		var ah = $(this).height();
		var ph = $(this).parent().height();
		var mh = (ph - ah) / 2;
		$(this).css('margin-top', mh);
		});
	};

	$.fn.stretchFormtasticInputWidthToParent = function() {
		return this.each(function(i){
		var p_width = $(this).closest("form").innerWidth();
		var p_padding = parseInt($(this).closest("form").css('padding-left') ,10) + parseInt($(this).closest("form").css('padding-right'), 10);
		var this_padding = parseInt($(this).css('padding-left'), 10) + parseInt($(this).css('padding-right'), 10);
		$(this).css('width', p_width - p_padding - this_padding);
		});
	};

	$('form.formtastic li.string input, form.formtastic textarea').stretchFormtasticInputWidthToParent();

	// Vertically center these paragraphs
	// Parent may need a min-height for this to work..
	$('ul.downplayed li div.content p').vAlign();

	// When a sandbox form is submitted..
	$("form.sandbox").submit(function(){

		var error_free = true;

		// Cycle through the forms required inputs
 		$(this).find("input.required").each(function() {

			// Remove any existing error styles from the input
			$(this).removeClass('error');

			// Tack the error style on if the input is empty..
			if ($(this).val() == '') {
				$(this).addClass('error');
				$(this).wiggle();
				error_free = false;
			}

		});

		return error_free;
	});

});

function clippyCopiedCallback(a) {
  $('#api_key_copied').fadeIn().delay(1000).fadeOut();

  // var b = $("#clippy_tooltip_" + a);
  // b.length != 0 && (b.attr("title", "copied!").trigger("tipsy.reload"), setTimeout(function() {
  //   b.attr("title", "copy to clipboard")
  // },
  // 500))
}

// Logging function that accounts for browsers that don't have window.console
log = function(){
  log.history = log.history || [];
  log.history.push(arguments);
  if(this.console){
    console.log( Array.prototype.slice.call(arguments)[0] );
  }
};

// Handle browsers that do console incorrectly (IE9 and below, see http://stackoverflow.com/a/5539378/7913)
if (Function.prototype.bind && console && typeof console.log == "object") {
    [
      "log","info","warn","error","assert","dir","clear","profile","profileEnd"
    ].forEach(function (method) {
        console[method] = this.bind(console[method], console);
    }, Function.prototype.call);
}

var Docs = {

	shebang: function() {

		// If shebang has an operation nickname in it..
		// e.g. /docs/#!/words/get_search
		var fragments = $.param.fragment().split('/');
		fragments.shift(); // get rid of the bang

		switch (fragments.length) {
			case 1:
				// Expand all operations for the resource and scroll to it
				var dom_id = 'resource_' + fragments[0];

				Docs.expandEndpointListForResource(fragments[0]);
				$("#"+dom_id).slideto({highlight: false});
				break;
			case 2:
				// Refer to the endpoint DOM element, e.g. #words_get_search

        // Expand Resource
        Docs.expandEndpointListForResource(fragments[0]);
        $("#"+dom_id).slideto({highlight: false});

        // Expand operation
				var li_dom_id = fragments.join('_');
				var li_content_dom_id = li_dom_id + "_content";


				Docs.expandOperation($('#'+li_content_dom_id));
				$('#'+li_dom_id).slideto({highlight: false});
				break;
		}

	},

	toggleEndpointListForResource: function(resource) {
		var elem = $('li#resource_' + Docs.escapeResourceName(resource) + ' ul.endpoints');
		if (elem.is(':visible')) {
			Docs.collapseEndpointListForResource(resource);
		} else {
			Docs.expandEndpointListForResource(resource);
		}
	},

	// Expand resource
	expandEndpointListForResource: function(resource) {
		var resource = Docs.escapeResourceName(resource);
		if (resource == '') {
			$('.resource ul.endpoints').slideDown();
			return;
		}
		
		$('li#resource_' + resource).addClass('active');

		var elem = $('li#resource_' + resource + ' ul.endpoints');
		elem.slideDown();
	},

	// Collapse resource and mark as explicitly closed
	collapseEndpointListForResource: function(resource) {
		var resource = Docs.escapeResourceName(resource);
		$('li#resource_' + resource).removeClass('active');

		var elem = $('li#resource_' + resource + ' ul.endpoints');
		elem.slideUp();
	},

	expandOperationsForResource: function(resource) {
		// Make sure the resource container is open..
		Docs.expandEndpointListForResource(resource);
		
		if (resource == '') {
			$('.resource ul.endpoints li.operation div.content').slideDown();
			return;
		}

		$('li#resource_' + Docs.escapeResourceName(resource) + ' li.operation div.content').each(function() {
			Docs.expandOperation($(this));
		});
	},

	collapseOperationsForResource: function(resource) {
		// Make sure the resource container is open..
		Docs.expandEndpointListForResource(resource);

		$('li#resource_' + Docs.escapeResourceName(resource) + ' li.operation div.content').each(function() {
			Docs.collapseOperation($(this));
		});
	},

	escapeResourceName: function(resource) {
		return resource.replace(/[!"#$%&'()*+,.\/:;<=>?@\[\\\]\^`{|}~]/g, "\\$&");
	},

	expandOperation: function(elem) {
		elem.slideDown();
	},

	collapseOperation: function(elem) {
		elem.slideUp();
	}
};(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
templates['apikey_button_view'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, functionType="function", escapeExpression=this.escapeExpression;


  buffer += "<div class='auth_button' id='apikey_button'><img class='auth_icon' alt='apply api key' src='images/apikey.jpeg'></div>\r\n<div class='auth_container' id='apikey_container'>\r\n  <div class='key_input_container'>\r\n    <div class='auth_label'>";
  if (stack1 = helpers.keyName) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.keyName; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</div>\r\n    <input placeholder=\"api_key\" class=\"auth_input\" id=\"input_apiKey_entry\" name=\"apiKey\" type=\"text\"/>\r\n    <div class='auth_submit'><a class='auth_submit_button' id=\"apply_api_key\" href=\"#\">apply</a></div>\r\n  </div>\r\n</div>\r\n\r\n";
  return buffer;
  });
})();

(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
templates['basic_auth_button_view'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  


  return "<div class='auth_button' id='basic_auth_button'><img class='auth_icon' src='images/password.jpeg'></div>\r\n<div class='auth_container' id='basic_auth_container'>\r\n  <div class='key_input_container'>\r\n    <div class=\"auth_label\">Username</div>\r\n    <input placeholder=\"username\" class=\"auth_input\" id=\"input_username\" name=\"username\" type=\"text\"/>\r\n    <div class=\"auth_label\">Password</div>\r\n    <input placeholder=\"password\" class=\"auth_input\" id=\"input_password\" name=\"password\" type=\"password\"/>\r\n    <div class='auth_submit'><a class='auth_submit_button' id=\"apply_basic_auth\" href=\"#\">apply</a></div>\r\n  </div>\r\n</div>\r\n\r\n";
  });
})();

(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
templates['content_type'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, functionType="function", self=this;

function program1(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n  ";
  stack1 = helpers.each.call(depth0, depth0.produces, {hash:{},inverse:self.noop,fn:self.program(2, program2, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n";
  return buffer;
  }
function program2(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n	<option value=\"";
  stack1 = (typeof depth0 === functionType ? depth0.apply(depth0) : depth0);
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\">";
  stack1 = (typeof depth0 === functionType ? depth0.apply(depth0) : depth0);
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "</option>\r\n	";
  return buffer;
  }

function program4(depth0,data) {
  
  
  return "\r\n  <option value=\"application/json\">application/json</option>\r\n";
  }

  buffer += "<label for=\"contentType\"></label>\r\n<select name=\"contentType\">\r\n";
  stack1 = helpers['if'].call(depth0, depth0.produces, {hash:{},inverse:self.program(4, program4, data),fn:self.program(1, program1, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n</select>\r\n";
  return buffer;
  });
})();

(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
templates['main'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, stack2, functionType="function", escapeExpression=this.escapeExpression, self=this;

function program1(depth0,data) {
  
  var buffer = "", stack1, stack2;
  buffer += "\r\n  <div class=\"info_title\">"
    + escapeExpression(((stack1 = ((stack1 = depth0.info),stack1 == null || stack1 === false ? stack1 : stack1.title)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "</div>\r\n  <div class=\"info_description\">";
  stack2 = ((stack1 = ((stack1 = depth0.info),stack1 == null || stack1 === false ? stack1 : stack1.description)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1);
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "</div>\r\n  ";
  stack2 = helpers['if'].call(depth0, ((stack1 = depth0.info),stack1 == null || stack1 === false ? stack1 : stack1.termsOfServiceUrl), {hash:{},inverse:self.noop,fn:self.program(2, program2, data),data:data});
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\r\n  ";
  stack2 = helpers['if'].call(depth0, ((stack1 = depth0.info),stack1 == null || stack1 === false ? stack1 : stack1.contact), {hash:{},inverse:self.noop,fn:self.program(4, program4, data),data:data});
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\r\n  ";
  stack2 = helpers['if'].call(depth0, ((stack1 = depth0.info),stack1 == null || stack1 === false ? stack1 : stack1.license), {hash:{},inverse:self.noop,fn:self.program(6, program6, data),data:data});
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\r\n  ";
  return buffer;
  }
function program2(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "<div class=\"info_tos\"><a href=\""
    + escapeExpression(((stack1 = ((stack1 = depth0.info),stack1 == null || stack1 === false ? stack1 : stack1.termsOfServiceUrl)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "\">Terms of service</a></div>";
  return buffer;
  }

function program4(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "<div class='info_contact'><a href=\"mailto:"
    + escapeExpression(((stack1 = ((stack1 = ((stack1 = depth0.info),stack1 == null || stack1 === false ? stack1 : stack1.contact)),stack1 == null || stack1 === false ? stack1 : stack1.name)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "\">Contact the developer</a></div>";
  return buffer;
  }

function program6(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "<div class='info_license'><a href='"
    + escapeExpression(((stack1 = ((stack1 = ((stack1 = depth0.info),stack1 == null || stack1 === false ? stack1 : stack1.license)),stack1 == null || stack1 === false ? stack1 : stack1.url)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "'>"
    + escapeExpression(((stack1 = ((stack1 = ((stack1 = depth0.info),stack1 == null || stack1 === false ? stack1 : stack1.license)),stack1 == null || stack1 === false ? stack1 : stack1.name)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "</a></div>";
  return buffer;
  }

function program8(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n    , <span style=\"font-variant: small-caps\">api version</span>: "
    + escapeExpression(((stack1 = ((stack1 = depth0.info),stack1 == null || stack1 === false ? stack1 : stack1.version)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "\r\n    ";
  return buffer;
  }

function program10(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n    <span style=\"float:right\"><a href=\"";
  if (stack1 = helpers.validatorUrl) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.validatorUrl; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "/debug?url=";
  if (stack1 = helpers.url) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.url; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "\"><img id=\"validator\" src=\"";
  if (stack1 = helpers.validatorUrl) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.validatorUrl; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "?url=";
  if (stack1 = helpers.url) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.url; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "\"></a>\r\n    </span>\r\n    ";
  return buffer;
  }

  buffer += "<div class='info' id='api_info'>\r\n  ";
  stack1 = helpers['if'].call(depth0, depth0.info, {hash:{},inverse:self.noop,fn:self.program(1, program1, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n</div>\r\n<div class='container' id='resources_container'>\r\n  <ul id='resources'></ul>\r\n\r\n  <div class=\"footer\">\r\n    <br>\r\n    <br>\r\n    <h4 style=\"color: #999\">[ <span style=\"font-variant: small-caps\">base url</span>: ";
  if (stack1 = helpers.basePath) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.basePath; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "\r\n    ";
  stack2 = helpers['if'].call(depth0, ((stack1 = depth0.info),stack1 == null || stack1 === false ? stack1 : stack1.version), {hash:{},inverse:self.noop,fn:self.program(8, program8, data),data:data});
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "]\r\n    ";
  stack2 = helpers['if'].call(depth0, depth0.validatorUrl, {hash:{},inverse:self.noop,fn:self.program(10, program10, data),data:data});
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\r\n    </h4>\r\n    </div>\r\n</div>\r\n";
  return buffer;
  });
})();

(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
templates['operation'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, options, functionType="function", escapeExpression=this.escapeExpression, self=this, blockHelperMissing=helpers.blockHelperMissing;

function program1(depth0,data) {
  
  
  return "deprecated";
  }

function program3(depth0,data) {
  
  
  return "\r\n            <h4>Warning: Deprecated</h4>\r\n        ";
  }

function program5(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n        <h4>Implementation Notes</h4>\r\n        <p>";
  if (stack1 = helpers.description) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.description; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "</p>\r\n        ";
  return buffer;
  }

function program7(depth0,data) {
  
  
  return "\r\n        <div class=\"auth\">\r\n        <span class=\"api-ic ic-error\"></span>";
  }

function program9(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n          <div id=\"api_information_panel\" style=\"top: 526px; left: 776px; display: none;\">\r\n          ";
  stack1 = helpers.each.call(depth0, depth0, {hash:{},inverse:self.noop,fn:self.program(10, program10, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n          </div>\r\n        ";
  return buffer;
  }
function program10(depth0,data) {
  
  var buffer = "", stack1, stack2;
  buffer += "\r\n            <div title='";
  stack2 = ((stack1 = depth0.description),typeof stack1 === functionType ? stack1.apply(depth0) : stack1);
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "'>"
    + escapeExpression(((stack1 = depth0.scope),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "</div>\r\n          ";
  return buffer;
  }

function program12(depth0,data) {
  
  
  return "</div>";
  }

function program14(depth0,data) {
  
  
  return "\r\n        <div class='access'>\r\n          <span class=\"api-ic ic-off\" title=\"click to authenticate\"></span>\r\n        </div>\r\n        ";
  }

function program16(depth0,data) {
  
  
  return "\r\n          <h4>Response Class</h4>\r\n          <p><span class=\"model-signature\" /></p>\r\n          <br/>\r\n          <div class=\"response-content-type\" />\r\n        ";
  }

function program18(depth0,data) {
  
  
  return "\r\n          <h4>Parameters</h4>\r\n          <table class='fullwidth'>\r\n          <thead>\r\n            <tr>\r\n            <th style=\"width: 100px; max-width: 100px\">Parameter</th>\r\n            <th style=\"width: 310px; max-width: 310px\">Value</th>\r\n            <th style=\"width: 200px; max-width: 200px\">Description</th>\r\n            <th style=\"width: 100px; max-width: 100px\">Parameter Type</th>\r\n            <th style=\"width: 220px; max-width: 230px\">Data Type</th>\r\n            </tr>\r\n          </thead>\r\n          <tbody class=\"operation-params\">\r\n\r\n          </tbody>\r\n          </table>\r\n          ";
  }

function program20(depth0,data) {
  
  
  return "\r\n          <div style='margin:0;padding:0;display:inline'></div>\r\n          <h4>Response Messages</h4>\r\n          <table class='fullwidth'>\r\n            <thead>\r\n            <tr>\r\n              <th>HTTP Status Code</th>\r\n              <th>Reason</th>\r\n              <th>Response Model</th>\r\n            </tr>\r\n            </thead>\r\n            <tbody class=\"operation-status\">\r\n            \r\n            </tbody>\r\n          </table>\r\n          ";
  }

function program22(depth0,data) {
  
  
  return "\r\n          ";
  }

function program24(depth0,data) {
  
  
  return "\r\n          <div class='sandbox_header'>\r\n            <input class='submit' name='commit' type='button' value='Try it out!' />\r\n            <a href='#' class='response_hider' style='display:none'>Hide Response</a>\r\n            <span class='response_throbber' style='display:none'></span>\r\n          </div>\r\n          ";
  }

  buffer += "\r\n  <ul class='operations' >\r\n    <li class='";
  if (stack1 = helpers.method) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.method; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + " operation' id='";
  if (stack1 = helpers.parentId) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.parentId; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "_";
  if (stack1 = helpers.nickname) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.nickname; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "'>\r\n      <div class='heading'>\r\n        <h3>\r\n          <span class='http_method'>\r\n          <a href='#!/";
  if (stack1 = helpers.parentId) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.parentId; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "/";
  if (stack1 = helpers.nickname) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.nickname; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "' class=\"toggleOperation\">";
  if (stack1 = helpers.method) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.method; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</a>\r\n          </span>\r\n          <span class='path'>\r\n          <a href='#!/";
  if (stack1 = helpers.parentId) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.parentId; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "/";
  if (stack1 = helpers.nickname) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.nickname; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "' class=\"toggleOperation ";
  stack1 = helpers['if'].call(depth0, depth0.deprecated, {hash:{},inverse:self.noop,fn:self.program(1, program1, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\">";
  if (stack1 = helpers.path) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.path; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</a>\r\n          </span>\r\n        </h3>\r\n        <ul class='options'>\r\n          <li>\r\n          <a href='#!/";
  if (stack1 = helpers.parentId) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.parentId; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "/";
  if (stack1 = helpers.nickname) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.nickname; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "' class=\"toggleOperation\">";
  if (stack1 = helpers.summary) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.summary; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "</a>\r\n          </li>\r\n        </ul>\r\n      </div>\r\n      <div class='content' id='";
  if (stack1 = helpers.parentId) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.parentId; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "_";
  if (stack1 = helpers.nickname) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.nickname; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "_content' style='display:none'>\r\n        ";
  stack1 = helpers['if'].call(depth0, depth0.deprecated, {hash:{},inverse:self.noop,fn:self.program(3, program3, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n        ";
  stack1 = helpers['if'].call(depth0, depth0.description, {hash:{},inverse:self.noop,fn:self.program(5, program5, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n        ";
  options = {hash:{},inverse:self.noop,fn:self.program(7, program7, data),data:data};
  if (stack1 = helpers.oauth) { stack1 = stack1.call(depth0, options); }
  else { stack1 = depth0.oauth; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  if (!helpers.oauth) { stack1 = blockHelperMissing.call(depth0, stack1, options); }
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n        ";
  stack1 = helpers.each.call(depth0, depth0.oauth, {hash:{},inverse:self.noop,fn:self.program(9, program9, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n        ";
  options = {hash:{},inverse:self.noop,fn:self.program(12, program12, data),data:data};
  if (stack1 = helpers.oauth) { stack1 = stack1.call(depth0, options); }
  else { stack1 = depth0.oauth; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  if (!helpers.oauth) { stack1 = blockHelperMissing.call(depth0, stack1, options); }
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n        ";
  options = {hash:{},inverse:self.noop,fn:self.program(14, program14, data),data:data};
  if (stack1 = helpers.oauth) { stack1 = stack1.call(depth0, options); }
  else { stack1 = depth0.oauth; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  if (!helpers.oauth) { stack1 = blockHelperMissing.call(depth0, stack1, options); }
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n        ";
  stack1 = helpers['if'].call(depth0, depth0.type, {hash:{},inverse:self.noop,fn:self.program(16, program16, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n        <form accept-charset='UTF-8' class='sandbox'>\r\n          <div style='margin:0;padding:0;display:inline'></div>\r\n          ";
  stack1 = helpers['if'].call(depth0, depth0.parameters, {hash:{},inverse:self.noop,fn:self.program(18, program18, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n          ";
  stack1 = helpers['if'].call(depth0, depth0.responseMessages, {hash:{},inverse:self.noop,fn:self.program(20, program20, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n          ";
  stack1 = helpers['if'].call(depth0, depth0.isReadOnly, {hash:{},inverse:self.program(24, program24, data),fn:self.program(22, program22, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n        </form>\r\n        <div class='response' style='display:none'>\r\n          <h4>Request URL</h4>\r\n          <div class='block request_url'></div>\r\n          <h4>Response Body</h4>\r\n          <div class='block response_body'></div>\r\n          <h4>Response Code</h4>\r\n          <div class='block response_code'></div>\r\n          <h4>Response Headers</h4>\r\n          <div class='block response_headers'></div>\r\n        </div>\r\n      </div>\r\n    </li>\r\n  </ul>\r\n";
  return buffer;
  });
})();

(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
templates['param'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, functionType="function", escapeExpression=this.escapeExpression, self=this;

function program1(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n		";
  stack1 = helpers['if'].call(depth0, depth0.isFile, {hash:{},inverse:self.program(4, program4, data),fn:self.program(2, program2, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n	";
  return buffer;
  }
function program2(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n			<input type=\"file\" name='";
  if (stack1 = helpers.name) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.name; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "'/>\r\n			<div class=\"parameter-content-type\" />\r\n		";
  return buffer;
  }

function program4(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n			";
  stack1 = helpers['if'].call(depth0, depth0['default'], {hash:{},inverse:self.program(7, program7, data),fn:self.program(5, program5, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n		";
  return buffer;
  }
function program5(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n				<textarea class='body-textarea' name='";
  if (stack1 = helpers.name) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.name; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "'>";
  if (stack1 = helpers['default']) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0['default']; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</textarea>\r\n			";
  return buffer;
  }

function program7(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n				<textarea class='body-textarea' name='";
  if (stack1 = helpers.name) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.name; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "'></textarea>\r\n				<br />\r\n				<div class=\"parameter-content-type\" />\r\n			";
  return buffer;
  }

function program9(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n		";
  stack1 = helpers['if'].call(depth0, depth0.isFile, {hash:{},inverse:self.program(10, program10, data),fn:self.program(2, program2, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n	";
  return buffer;
  }
function program10(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n			";
  stack1 = helpers['if'].call(depth0, depth0['default'], {hash:{},inverse:self.program(13, program13, data),fn:self.program(11, program11, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n		";
  return buffer;
  }
function program11(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n				<input class='parameter' minlength='0' name='";
  if (stack1 = helpers.name) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.name; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "' placeholder='' type='text' value='";
  if (stack1 = helpers['default']) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0['default']; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "'/>\r\n			";
  return buffer;
  }

function program13(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n				<input class='parameter' minlength='0' name='";
  if (stack1 = helpers.name) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.name; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "' placeholder='' type='text' value=''/>\r\n			";
  return buffer;
  }

  buffer += "<td class='code'>";
  if (stack1 = helpers.name) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.name; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</td>\r\n<td>\r\n\r\n	";
  stack1 = helpers['if'].call(depth0, depth0.isBody, {hash:{},inverse:self.program(9, program9, data),fn:self.program(1, program1, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n\r\n</td>\r\n<td>";
  if (stack1 = helpers.description) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.description; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "</td>\r\n<td>";
  if (stack1 = helpers.paramType) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.paramType; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "</td>\r\n<td>\r\n	<span class=\"model-signature\"></span>\r\n</td>\r\n";
  return buffer;
  });
})();

(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
templates['parameter_content_type'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, functionType="function", self=this;

function program1(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n  ";
  stack1 = helpers.each.call(depth0, depth0.consumes, {hash:{},inverse:self.noop,fn:self.program(2, program2, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n";
  return buffer;
  }
function program2(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n  <option value=\"";
  stack1 = (typeof depth0 === functionType ? depth0.apply(depth0) : depth0);
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\">";
  stack1 = (typeof depth0 === functionType ? depth0.apply(depth0) : depth0);
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "</option>\r\n  ";
  return buffer;
  }

function program4(depth0,data) {
  
  
  return "\r\n  <option value=\"application/json\">application/json</option>\r\n";
  }

  buffer += "<label for=\"parameterContentType\"></label>\r\n<select name=\"parameterContentType\">\r\n";
  stack1 = helpers['if'].call(depth0, depth0.consumes, {hash:{},inverse:self.program(4, program4, data),fn:self.program(1, program1, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n</select>\r\n";
  return buffer;
  });
})();

(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
templates['param_list'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, stack2, options, self=this, helperMissing=helpers.helperMissing, functionType="function", escapeExpression=this.escapeExpression;

function program1(depth0,data) {
  
  
  return " multiple='multiple'";
  }

function program3(depth0,data) {
  
  
  return "\r\n    ";
  }

function program5(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n      ";
  stack1 = helpers['if'].call(depth0, depth0['default'], {hash:{},inverse:self.program(8, program8, data),fn:self.program(6, program6, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n    ";
  return buffer;
  }
function program6(depth0,data) {
  
  
  return "\r\n      ";
  }

function program8(depth0,data) {
  
  var buffer = "", stack1, stack2, options;
  buffer += "\r\n        ";
  options = {hash:{},inverse:self.program(11, program11, data),fn:self.program(9, program9, data),data:data};
  stack2 = ((stack1 = helpers.isArray || depth0.isArray),stack1 ? stack1.call(depth0, depth0, options) : helperMissing.call(depth0, "isArray", depth0, options));
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\r\n      ";
  return buffer;
  }
function program9(depth0,data) {
  
  
  return "\r\n        ";
  }

function program11(depth0,data) {
  
  
  return "\r\n          <option selected=\"\" value=''></option>\r\n        ";
  }

function program13(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n      ";
  stack1 = helpers['if'].call(depth0, depth0.isDefault, {hash:{},inverse:self.program(16, program16, data),fn:self.program(14, program14, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n    ";
  return buffer;
  }
function program14(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n        <option selected=\"\" value='";
  if (stack1 = helpers.value) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.value; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "'>";
  if (stack1 = helpers.value) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.value; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + " (default)</option>\r\n      ";
  return buffer;
  }

function program16(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n        <option value='";
  if (stack1 = helpers.value) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.value; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "'>";
  if (stack1 = helpers.value) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.value; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</option>\r\n      ";
  return buffer;
  }

  buffer += "<td class='code'>";
  if (stack1 = helpers.name) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.name; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</td>\r\n<td>\r\n  <select ";
  options = {hash:{},inverse:self.noop,fn:self.program(1, program1, data),data:data};
  stack2 = ((stack1 = helpers.isArray || depth0.isArray),stack1 ? stack1.call(depth0, depth0, options) : helperMissing.call(depth0, "isArray", depth0, options));
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += " class='parameter' name='";
  if (stack2 = helpers.name) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.name; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  buffer += escapeExpression(stack2)
    + "'>\r\n    ";
  stack2 = helpers['if'].call(depth0, depth0.required, {hash:{},inverse:self.program(5, program5, data),fn:self.program(3, program3, data),data:data});
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\r\n    ";
  stack2 = helpers.each.call(depth0, ((stack1 = depth0.allowableValues),stack1 == null || stack1 === false ? stack1 : stack1.descriptiveValues), {hash:{},inverse:self.noop,fn:self.program(13, program13, data),data:data});
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\r\n  </select>\r\n</td>\r\n<td>";
  if (stack2 = helpers.description) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.description; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "</td>\r\n<td>";
  if (stack2 = helpers.paramType) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.paramType; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "</td>\r\n<td><span class=\"model-signature\"></span></td>";
  return buffer;
  });
})();

(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
templates['param_readonly'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, functionType="function", escapeExpression=this.escapeExpression, self=this;

function program1(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n        <textarea class='body-textarea' readonly='readonly' name='";
  if (stack1 = helpers.name) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.name; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "'>";
  if (stack1 = helpers['default']) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0['default']; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</textarea>\r\n    ";
  return buffer;
  }

function program3(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n        ";
  stack1 = helpers['if'].call(depth0, depth0['default'], {hash:{},inverse:self.program(6, program6, data),fn:self.program(4, program4, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n    ";
  return buffer;
  }
function program4(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n            ";
  if (stack1 = helpers['default']) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0['default']; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "\r\n        ";
  return buffer;
  }

function program6(depth0,data) {
  
  
  return "\r\n            (empty)\r\n        ";
  }

  buffer += "<td class='code'>";
  if (stack1 = helpers.name) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.name; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</td>\r\n<td>\r\n    ";
  stack1 = helpers['if'].call(depth0, depth0.isBody, {hash:{},inverse:self.program(3, program3, data),fn:self.program(1, program1, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n</td>\r\n<td>";
  if (stack1 = helpers.description) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.description; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "</td>\r\n<td>";
  if (stack1 = helpers.paramType) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.paramType; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "</td>\r\n<td><span class=\"model-signature\"></span></td>\r\n";
  return buffer;
  });
})();

(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
templates['param_readonly_required'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, functionType="function", escapeExpression=this.escapeExpression, self=this;

function program1(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n        <textarea class='body-textarea'  readonly='readonly' placeholder='(required)' name='";
  if (stack1 = helpers.name) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.name; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "'>";
  if (stack1 = helpers['default']) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0['default']; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</textarea>\r\n    ";
  return buffer;
  }

function program3(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n        ";
  stack1 = helpers['if'].call(depth0, depth0['default'], {hash:{},inverse:self.program(6, program6, data),fn:self.program(4, program4, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n    ";
  return buffer;
  }
function program4(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n            ";
  if (stack1 = helpers['default']) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0['default']; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "\r\n        ";
  return buffer;
  }

function program6(depth0,data) {
  
  
  return "\r\n            (empty)\r\n        ";
  }

  buffer += "<td class='code required'>";
  if (stack1 = helpers.name) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.name; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</td>\r\n<td>\r\n    ";
  stack1 = helpers['if'].call(depth0, depth0.isBody, {hash:{},inverse:self.program(3, program3, data),fn:self.program(1, program1, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n</td>\r\n<td>";
  if (stack1 = helpers.description) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.description; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "</td>\r\n<td>";
  if (stack1 = helpers.paramType) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.paramType; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "</td>\r\n<td><span class=\"model-signature\"></span></td>\r\n";
  return buffer;
  });
})();

(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
templates['param_required'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, functionType="function", escapeExpression=this.escapeExpression, self=this;

function program1(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n		";
  stack1 = helpers['if'].call(depth0, depth0.isFile, {hash:{},inverse:self.program(4, program4, data),fn:self.program(2, program2, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n	";
  return buffer;
  }
function program2(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n			<input type=\"file\" name='";
  if (stack1 = helpers.name) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.name; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "'/>\r\n		";
  return buffer;
  }

function program4(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n			";
  stack1 = helpers['if'].call(depth0, depth0['default'], {hash:{},inverse:self.program(7, program7, data),fn:self.program(5, program5, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n		";
  return buffer;
  }
function program5(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n				<textarea class='body-textarea required' placeholder='(required)' name='";
  if (stack1 = helpers.name) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.name; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "'>";
  if (stack1 = helpers['default']) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0['default']; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</textarea>\r\n			";
  return buffer;
  }

function program7(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n				<textarea class='body-textarea required' placeholder='(required)' name='";
  if (stack1 = helpers.name) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.name; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "'></textarea>\r\n				<br />\r\n				<div class=\"parameter-content-type\" />\r\n			";
  return buffer;
  }

function program9(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n		";
  stack1 = helpers['if'].call(depth0, depth0.isFile, {hash:{},inverse:self.program(12, program12, data),fn:self.program(10, program10, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n	";
  return buffer;
  }
function program10(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n			<input class='parameter' class='required' type='file' name='";
  if (stack1 = helpers.name) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.name; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "'/>\r\n		";
  return buffer;
  }

function program12(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n			";
  stack1 = helpers['if'].call(depth0, depth0['default'], {hash:{},inverse:self.program(15, program15, data),fn:self.program(13, program13, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n		";
  return buffer;
  }
function program13(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n				<input class='parameter required' minlength='1' name='";
  if (stack1 = helpers.name) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.name; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "' placeholder='(required)' type='text' value='";
  if (stack1 = helpers['default']) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0['default']; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "'/>\r\n			";
  return buffer;
  }

function program15(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n				<input class='parameter required' minlength='1' name='";
  if (stack1 = helpers.name) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.name; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "' placeholder='(required)' type='text' value=''/>\r\n			";
  return buffer;
  }

  buffer += "<td class='code required'>";
  if (stack1 = helpers.name) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.name; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</td>\r\n<td>\r\n	";
  stack1 = helpers['if'].call(depth0, depth0.isBody, {hash:{},inverse:self.program(9, program9, data),fn:self.program(1, program1, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n</td>\r\n<td>\r\n	<strong>";
  if (stack1 = helpers.description) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.description; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "</strong>\r\n</td>\r\n<td>";
  if (stack1 = helpers.paramType) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.paramType; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "</td>\r\n<td><span class=\"model-signature\"></span></td>\r\n";
  return buffer;
  });
})();

(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
templates['resource'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, options, functionType="function", escapeExpression=this.escapeExpression, self=this, blockHelperMissing=helpers.blockHelperMissing;

function program1(depth0,data) {
  
  
  return " : ";
  }

function program3(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "<li>\r\n      <a href='";
  if (stack1 = helpers.url) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.url; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "'>Raw</a>\r\n    </li>";
  return buffer;
  }

  buffer += "<div class='heading'>\r\n  <h2>\r\n    <a href='#!/";
  if (stack1 = helpers.id) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.id; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "' class=\"toggleEndpointList\" data-id=\"";
  if (stack1 = helpers.id) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.id; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "\">";
  if (stack1 = helpers.name) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.name; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</a> ";
  options = {hash:{},inverse:self.noop,fn:self.program(1, program1, data),data:data};
  if (stack1 = helpers.summary) { stack1 = stack1.call(depth0, options); }
  else { stack1 = depth0.summary; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  if (!helpers.summary) { stack1 = blockHelperMissing.call(depth0, stack1, options); }
  if(stack1 || stack1 === 0) { buffer += stack1; }
  if (stack1 = helpers.summary) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.summary; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n  </h2>\r\n  <ul class='options'>\r\n    <li>\r\n      <a href='#!/";
  if (stack1 = helpers.id) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.id; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "' id='endpointListTogger_";
  if (stack1 = helpers.id) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.id; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "' class=\"toggleEndpointList\" data-id=\"";
  if (stack1 = helpers.id) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.id; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "\">Show/Hide</a>\r\n    </li>\r\n    <li>\r\n      <a href='#' class=\"collapseResource\" data-id=\"";
  if (stack1 = helpers.id) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.id; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "\">\r\n        List Operations\r\n      </a>\r\n    </li>\r\n    <li>\r\n      <a href='#' class=\"expandResource\" data-id=\"";
  if (stack1 = helpers.id) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.id; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "\">\r\n        Expand Operations\r\n      </a>\r\n    </li>\r\n    ";
  options = {hash:{},inverse:self.noop,fn:self.program(3, program3, data),data:data};
  if (stack1 = helpers.url) { stack1 = stack1.call(depth0, options); }
  else { stack1 = depth0.url; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  if (!helpers.url) { stack1 = blockHelperMissing.call(depth0, stack1, options); }
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n  </ul>\r\n</div>\r\n<ul class='endpoints' id='";
  if (stack1 = helpers.id) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.id; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "_endpoint_list' style='display:none'>\r\n\r\n</ul>\r\n";
  return buffer;
  });
})();

(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
templates['response_content_type'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, functionType="function", self=this;

function program1(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n  ";
  stack1 = helpers.each.call(depth0, depth0.produces, {hash:{},inverse:self.noop,fn:self.program(2, program2, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n";
  return buffer;
  }
function program2(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n  <option value=\"";
  stack1 = (typeof depth0 === functionType ? depth0.apply(depth0) : depth0);
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\">";
  stack1 = (typeof depth0 === functionType ? depth0.apply(depth0) : depth0);
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "</option>\r\n  ";
  return buffer;
  }

function program4(depth0,data) {
  
  
  return "\r\n  <option value=\"application/json\">application/json</option>\r\n";
  }

  buffer += "<label for=\"responseContentType\"></label>\r\n<select name=\"responseContentType\">\r\n";
  stack1 = helpers['if'].call(depth0, depth0.produces, {hash:{},inverse:self.program(4, program4, data),fn:self.program(1, program1, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n</select>\r\n";
  return buffer;
  });
})();

(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
templates['signature'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, functionType="function", escapeExpression=this.escapeExpression;


  buffer += "<div>\r\n<ul class=\"signature-nav\">\r\n  <li><a class=\"description-link\" href=\"#\">Model</a></li>\r\n  <li><a class=\"snippet-link\" href=\"#\">Model Schema</a></li>\r\n</ul>\r\n<div>\r\n\r\n<div class=\"signature-container\">\r\n  <div class=\"description\">\r\n    ";
  if (stack1 = helpers.signature) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.signature; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n  </div>\r\n\r\n  <div class=\"snippet\">\r\n    <pre><code>";
  if (stack1 = helpers.sampleJSON) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.sampleJSON; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</code></pre>\r\n    <small class=\"notice\"></small>\r\n  </div>\r\n</div>\r\n\r\n";
  return buffer;
  });
})();

(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
templates['status_code'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, functionType="function", escapeExpression=this.escapeExpression;


  buffer += "<td width='15%' class='code'>";
  if (stack1 = helpers.code) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.code; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</td>\r\n<td>";
  if (stack1 = helpers.message) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.message; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "</td>\r\n<td width='50%'><span class=\"model-signature\" /></td>";
  return buffer;
  });
})();



// Generated by CoffeeScript 1.8.0
(function() {
  var ApiKeyButton, BasicAuthButton, ContentTypeView, HeaderView, MainView, OperationView, ParameterContentTypeView, ParameterView, ResourceView, ResponseContentTypeView, SignatureView, StatusCodeView, SwaggerUi,
    __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; };

  SwaggerUi = (function(_super) {
    __extends(SwaggerUi, _super);

    function SwaggerUi() {
      return SwaggerUi.__super__.constructor.apply(this, arguments);
    }

    SwaggerUi.prototype.dom_id = "swagger_ui";

    SwaggerUi.prototype.options = null;

    SwaggerUi.prototype.api = null;

    SwaggerUi.prototype.headerView = null;

    SwaggerUi.prototype.mainView = null;

    SwaggerUi.prototype.initialize = function(options) {
      if (options == null) {
        options = {};
      }
      if (options.dom_id != null) {
        this.dom_id = options.dom_id;
        delete options.dom_id;
      }
      if ($('#' + this.dom_id) == null) {
        $('body').append('<div id="' + this.dom_id + '"></div>');
      }
      this.options = options;
      this.options.success = (function(_this) {
        return function() {
          return _this.render();
        };
      })(this);
      this.options.progress = (function(_this) {
        return function(d) {
          return _this.showMessage(d);
        };
      })(this);
      this.options.failure = (function(_this) {
        return function(d) {
          if (_this.api && _this.api.isValid === false) {
            log("not a valid 2.0 spec, loading legacy client");
            _this.api = new SwaggerApi(_this.options);
            return _this.api.build();
          } else {
            return _this.onLoadFailure(d);
          }
        };
      })(this);
      this.headerView = new HeaderView({
        el: $('#header')
      });
      return this.headerView.on('update-swagger-ui', (function(_this) {
        return function(data) {
          return _this.updateSwaggerUi(data);
        };
      })(this));
    };

    SwaggerUi.prototype.setOption = function(option, value) {
      return this.options[option] = value;
    };

    SwaggerUi.prototype.getOption = function(option) {
      return this.options[option];
    };

    SwaggerUi.prototype.updateSwaggerUi = function(data) {
      this.options.url = data.url;
      return this.load();
    };

    SwaggerUi.prototype.load = function() {
      var url, _ref;
      if ((_ref = this.mainView) != null) {
        _ref.clear();
      }
      url = this.options.url;
      if (url.indexOf("http") !== 0) {
        url = this.buildUrl(window.location.href.toString(), url);
      }
      this.options.url = url;
      this.headerView.update(url);
      this.api = new SwaggerClient(this.options);
      return this.api.build();
    };

    SwaggerUi.prototype.collapseAll = function() {
      return Docs.collapseEndpointListForResource('');
    };

    SwaggerUi.prototype.listAll = function() {
      return Docs.collapseOperationsForResource('');
    };

    SwaggerUi.prototype.expandAll = function() {
      return Docs.expandOperationsForResource('');
    };

    SwaggerUi.prototype.render = function() {
      this.showMessage('Finished Loading Resource Information. Rendering Swagger UI...');
      this.mainView = new MainView({
        model: this.api,
        el: $('#' + this.dom_id),
        swaggerOptions: this.options
      }).render();
      this.showMessage();
      switch (this.options.docExpansion) {
        case "full":
          this.expandAll();
          break;
        case "list":
          this.listAll();
      }
      if (this.options.onComplete) {
        this.options.onComplete(this.api, this);
      }
      return setTimeout((function(_this) {
        return function() {
          return Docs.shebang();
        };
      })(this), 400);
    };

    SwaggerUi.prototype.buildUrl = function(base, url) {
      var endOfPath, parts;
      log("base is " + base);
      if (url.indexOf("/") === 0) {
        parts = base.split("/");
        base = parts[0] + "//" + parts[2];
        return base + url;
      } else {
        endOfPath = base.length;
        if (base.indexOf("?") > -1) {
          endOfPath = Math.min(endOfPath, base.indexOf("?"));
        }
        if (base.indexOf("#") > -1) {
          endOfPath = Math.min(endOfPath, base.indexOf("#"));
        }
        base = base.substring(0, endOfPath);
        if (base.indexOf("/", base.length - 1) !== -1) {
          return base + url;
        }
        return base + "/" + url;
      }
    };

    SwaggerUi.prototype.showMessage = function(data) {
      if (data == null) {
        data = '';
      }
      $('#message-bar').removeClass('message-fail');
      $('#message-bar').addClass('message-success');
      return $('#message-bar').html(data);
    };

    SwaggerUi.prototype.onLoadFailure = function(data) {
      var val;
      if (data == null) {
        data = '';
      }
      $('#message-bar').removeClass('message-success');
      $('#message-bar').addClass('message-fail');
      val = $('#message-bar').html(data);
      if (this.options.onFailure != null) {
        this.options.onFailure(data);
      }
      return val;
    };

    return SwaggerUi;

  })(Backbone.Router);

  window.SwaggerUi = SwaggerUi;

  HeaderView = (function(_super) {
    __extends(HeaderView, _super);

    function HeaderView() {
      return HeaderView.__super__.constructor.apply(this, arguments);
    }

    HeaderView.prototype.events = {
      'click #show-pet-store-icon': 'showPetStore',
      'click #show-wordnik-dev-icon': 'showWordnikDev',
      'click #explore': 'showCustom',
      'keyup #input_baseUrl': 'showCustomOnKeyup',
      'keyup #input_apiKey': 'showCustomOnKeyup'
    };

    HeaderView.prototype.initialize = function() {};

    HeaderView.prototype.showPetStore = function(e) {
      return this.trigger('update-swagger-ui', {
        url: "http://petstore.swagger.wordnik.com/api/api-docs"
      });
    };

    HeaderView.prototype.showWordnikDev = function(e) {
      return this.trigger('update-swagger-ui', {
        url: "http://api.wordnik.com/v4/resources.json"
      });
    };

    HeaderView.prototype.showCustomOnKeyup = function(e) {
      if (e.keyCode === 13) {
        return this.showCustom();
      }
    };

    HeaderView.prototype.showCustom = function(e) {
      if (e != null) {
        e.preventDefault();
      }
      return this.trigger('update-swagger-ui', {
        url: $('#input_baseUrl').val(),
        apiKey: $('#input_apiKey').val()
      });
    };

    HeaderView.prototype.update = function(url, apiKey, trigger) {
      if (trigger == null) {
        trigger = false;
      }
      $('#input_baseUrl').val(url);
      if (trigger) {
        return this.trigger('update-swagger-ui', {
          url: url
        });
      }
    };

    return HeaderView;

  })(Backbone.View);

  MainView = (function(_super) {
    var sorters;

    __extends(MainView, _super);

    function MainView() {
      return MainView.__super__.constructor.apply(this, arguments);
    }

    sorters = {
      'alpha': function(a, b) {
        return a.path.localeCompare(b.path);
      },
      'method': function(a, b) {
        return a.method.localeCompare(b.method);
      }
    };

    MainView.prototype.initialize = function(opts) {
      var auth, key, name, url, value, _ref;
      if (opts == null) {
        opts = {};
      }
      this.model.auths = [];
      _ref = this.model.securityDefinitions;
      for (key in _ref) {
        value = _ref[key];
        auth = {
          name: key,
          type: value.type,
          value: value
        };
        this.model.auths.push(auth);
      }
      if (this.model.info && this.model.info.license && typeof this.model.info.license === 'string') {
        name = this.model.info.license;
        url = this.model.info.licenseUrl;
        this.model.info.license = {};
        this.model.info.license.name = name;
        this.model.info.license.url = url;
      }
      if (!this.model.info) {
        this.model.info = {};
      }
      if (!this.model.info.version) {
        this.model.info.version = this.model.apiVersion;
      }
      if (this.model.swaggerVersion === "2.0") {
        if ("validatorUrl" in opts.swaggerOptions) {
          return this.model.validatorUrl = opts.swaggerOptions.validatorUrl;
        } else if (this.model.url.indexOf("localhost") > 0) {
          return this.model.validatorUrl = null;
        } else {
          return this.model.validatorUrl = "http://online.swagger.io/validator";
        }
      }
    };

    MainView.prototype.render = function() {
      var auth, button, counter, id, name, resource, resources, _i, _len, _ref;
      if (this.model.securityDefinitions) {
        for (name in this.model.securityDefinitions) {
          auth = this.model.securityDefinitions[name];
          if (auth.type === "apiKey" && $("#apikey_button").length === 0) {
            button = new ApiKeyButton({
              model: auth
            }).render().el;
            $('.auth_main_container').append(button);
          }
          if (auth.type === "basicAuth" && $("#basic_auth_button").length === 0) {
            button = new BasicAuthButton({
              model: auth
            }).render().el;
            $('.auth_main_container').append(button);
          }
        }
      }
      $(this.el).html(Handlebars.templates.main(this.model));
      resources = {};
      counter = 0;
      _ref = this.model.apisArray;
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        resource = _ref[_i];
        id = resource.name;
        while (typeof resources[id] !== 'undefined') {
          id = id + "_" + counter;
          counter += 1;
        }
        resource.id = id;
        resources[id] = resource;
        this.addResource(resource, this.model.auths);
      }
      return this;
    };

    MainView.prototype.addResource = function(resource, auths) {
      var resourceView;
      resource.id = resource.id.replace(/\s/g, '_');
      resourceView = new ResourceView({
        model: resource,
        tagName: 'li',
        id: 'resource_' + resource.id,
        className: 'resource',
        auths: auths,
        swaggerOptions: this.options.swaggerOptions
      });
      return $('#resources').append(resourceView.render().el);
    };

    MainView.prototype.clear = function() {
      return $(this.el).html('');
    };

    return MainView;

  })(Backbone.View);

  ResourceView = (function(_super) {
    __extends(ResourceView, _super);

    function ResourceView() {
      return ResourceView.__super__.constructor.apply(this, arguments);
    }

    ResourceView.prototype.initialize = function(opts) {
      if (opts == null) {
        opts = {};
      }
      this.auths = opts.auths;
      if ("" === this.model.description) {
        return this.model.description = null;
      }
    };

    ResourceView.prototype.render = function() {
      var counter, id, methods, operation, _i, _len, _ref;
      $(this.el).html(Handlebars.templates.resource(this.model));
      methods = {};
      if (this.model.description) {
        this.model.summary = this.model.description;
      }
      _ref = this.model.operationsArray;
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        operation = _ref[_i];
        counter = 0;
        id = operation.nickname;
        while (typeof methods[id] !== 'undefined') {
          id = id + "_" + counter;
          counter += 1;
        }
        methods[id] = operation;
        operation.nickname = id;
        operation.parentId = this.model.id;
        this.addOperation(operation);
      }
      $('.toggleEndpointList', this.el).click(this.callDocs.bind(this, 'toggleEndpointListForResource'));
      $('.collapseResource', this.el).click(this.callDocs.bind(this, 'collapseOperationsForResource'));
      $('.expandResource', this.el).click(this.callDocs.bind(this, 'expandOperationsForResource'));
      return this;
    };

    ResourceView.prototype.addOperation = function(operation) {
      var operationView;
      operation.number = this.number;
      operationView = new OperationView({
        model: operation,
        tagName: 'li',
        className: 'endpoint',
        swaggerOptions: this.options.swaggerOptions,
        auths: this.auths
      });
      $('.endpoints', $(this.el)).append(operationView.render().el);
      return this.number++;
    };

    ResourceView.prototype.callDocs = function(fnName, e) {
      e.preventDefault();
      return Docs[fnName](e.currentTarget.getAttribute('data-id'));
    };

    return ResourceView;

  })(Backbone.View);

  OperationView = (function(_super) {
    __extends(OperationView, _super);

    function OperationView() {
      return OperationView.__super__.constructor.apply(this, arguments);
    }

    OperationView.prototype.invocationUrl = null;

    OperationView.prototype.events = {
      'submit .sandbox': 'submitOperation',
      'click .submit': 'submitOperation',
      'click .response_hider': 'hideResponse',
      'click .toggleOperation': 'toggleOperationContent',
      'mouseenter .api-ic': 'mouseEnter',
      'mouseout .api-ic': 'mouseExit'
    };

    OperationView.prototype.initialize = function(opts) {
      if (opts == null) {
        opts = {};
      }
      this.auths = opts.auths;
      return this;
    };

    OperationView.prototype.mouseEnter = function(e) {
      var elem, hgh, pos, scMaxX, scMaxY, scX, scY, wd, x, y;
      elem = $(e.currentTarget.parentNode).find('#api_information_panel');
      x = e.pageX;
      y = e.pageY;
      scX = $(window).scrollLeft();
      scY = $(window).scrollTop();
      scMaxX = scX + $(window).width();
      scMaxY = scY + $(window).height();
      wd = elem.width();
      hgh = elem.height();
      if (x + wd > scMaxX) {
        x = scMaxX - wd;
      }
      if (x < scX) {
        x = scX;
      }
      if (y + hgh > scMaxY) {
        y = scMaxY - hgh;
      }
      if (y < scY) {
        y = scY;
      }
      pos = {};
      pos.top = y;
      pos.left = x;
      elem.css(pos);
      return $(e.currentTarget.parentNode).find('#api_information_panel').show();
    };

    OperationView.prototype.mouseExit = function(e) {
      return $(e.currentTarget.parentNode).find('#api_information_panel').hide();
    };

    OperationView.prototype.render = function() {
      var a, auth, auths, code, contentTypeModel, isMethodSubmissionSupported, k, key, o, param, ref, responseContentTypeView, responseSignatureView, schema, schemaObj, signatureModel, statusCode, type, v, value, _i, _j, _k, _l, _len, _len1, _len2, _len3, _len4, _m, _ref, _ref1, _ref2, _ref3, _ref4, _ref5, _ref6;
      isMethodSubmissionSupported = true;
      if (!isMethodSubmissionSupported) {
        this.model.isReadOnly = true;
      }
      this.model.description = this.model.description || this.model.notes;
      if (this.model.description) {
        this.model.description = this.model.description.replace(/(?:\r\n|\r|\n)/g, '<br />');
      }
      this.model.oauth = null;
      if (this.model.authorizations) {
        if (Array.isArray(this.model.authorizations)) {
          _ref = this.model.authorizations;
          for (_i = 0, _len = _ref.length; _i < _len; _i++) {
            auths = _ref[_i];
            for (key in auths) {
              auth = auths[key];
              for (a in this.auths) {
                auth = this.auths[a];
                if (auth.type === 'oauth2') {
                  this.model.oauth = {};
                  this.model.oauth.scopes = [];
                  _ref1 = auth.value.scopes;
                  for (k in _ref1) {
                    v = _ref1[k];
                    o = {
                      scope: k,
                      description: v
                    };
                    this.model.oauth.scopes.push(o);
                  }
                }
              }
            }
          }
        } else {
          _ref2 = this.model.authorizations;
          for (k in _ref2) {
            v = _ref2[k];
            if (k === "oauth2") {
              if (this.model.oauth === null) {
                this.model.oauth = {};
              }
              if (this.model.oauth.scopes === void 0) {
                this.model.oauth.scopes = [];
              }
              for (_j = 0, _len1 = v.length; _j < _len1; _j++) {
                o = v[_j];
                this.model.oauth.scopes.push(o);
              }
            }
          }
        }
      }
      if (typeof this.model.responses !== 'undefined') {
        this.model.responseMessages = [];
        _ref3 = this.model.responses;
        for (code in _ref3) {
          value = _ref3[code];
          schema = null;
          schemaObj = this.model.responses[code].schema;
          if (schemaObj && schemaObj['$ref']) {
            schema = schemaObj['$ref'];
            if (schema.indexOf('#/definitions/') === 0) {
              schema = schema.substring('#/definitions/'.length);
            }
          }
          this.model.responseMessages.push({
            code: code,
            message: value.description,
            responseModel: schema
          });
        }
      }
      if (typeof this.model.responseMessages === 'undefined') {
        this.model.responseMessages = [];
      }
      $(this.el).html(Handlebars.templates.operation(this.model));
      if (this.model.responseClassSignature && this.model.responseClassSignature !== 'string') {
        signatureModel = {
          sampleJSON: this.model.responseSampleJSON,
          isParam: false,
          signature: this.model.responseClassSignature
        };
        responseSignatureView = new SignatureView({
          model: signatureModel,
          tagName: 'div'
        });
        $('.model-signature', $(this.el)).append(responseSignatureView.render().el);
      } else {
        this.model.responseClassSignature = 'string';
        $('.model-signature', $(this.el)).html(this.model.type);
      }
      contentTypeModel = {
        isParam: false
      };
      contentTypeModel.consumes = this.model.consumes;
      contentTypeModel.produces = this.model.produces;
      _ref4 = this.model.parameters;
      for (_k = 0, _len2 = _ref4.length; _k < _len2; _k++) {
        param = _ref4[_k];
        type = param.type || param.dataType;
        if (typeof type === 'undefined') {
          schema = param.schema;
          if (schema && schema['$ref']) {
            ref = schema['$ref'];
            if (ref.indexOf('#/definitions/') === 0) {
              type = ref.substring('#/definitions/'.length);
            } else {
              type = ref;
            }
          }
        }
        if (type && type.toLowerCase() === 'file') {
          if (!contentTypeModel.consumes) {
            contentTypeModel.consumes = 'multipart/form-data';
          }
        }
        param.type = type;
      }
      responseContentTypeView = new ResponseContentTypeView({
        model: contentTypeModel
      });
      $('.response-content-type', $(this.el)).append(responseContentTypeView.render().el);
      _ref5 = this.model.parameters;
      for (_l = 0, _len3 = _ref5.length; _l < _len3; _l++) {
        param = _ref5[_l];
        this.addParameter(param, contentTypeModel.consumes);
      }
      _ref6 = this.model.responseMessages;
      for (_m = 0, _len4 = _ref6.length; _m < _len4; _m++) {
        statusCode = _ref6[_m];
        this.addStatusCode(statusCode);
      }
      return this;
    };

    OperationView.prototype.addParameter = function(param, consumes) {
      var paramView;
      param.consumes = consumes;
      paramView = new ParameterView({
        model: param,
        tagName: 'tr',
        readOnly: this.model.isReadOnly
      });
      return $('.operation-params', $(this.el)).append(paramView.render().el);
    };

    OperationView.prototype.addStatusCode = function(statusCode) {
      var statusCodeView;
      statusCodeView = new StatusCodeView({
        model: statusCode,
        tagName: 'tr'
      });
      return $('.operation-status', $(this.el)).append(statusCodeView.render().el);
    };

    OperationView.prototype.submitOperation = function(e) {
      var error_free, form, isFileUpload, map, o, opts, val, _i, _j, _k, _len, _len1, _len2, _ref, _ref1, _ref2;
      if (e != null) {
        e.preventDefault();
      }
      form = $('.sandbox', $(this.el));
      error_free = true;
      form.find("input.required").each(function() {
        $(this).removeClass("error");
        if (jQuery.trim($(this).val()) === "") {
          $(this).addClass("error");
          $(this).wiggle({
            callback: (function(_this) {
              return function() {
                return $(_this).focus();
              };
            })(this)
          });
          return error_free = false;
        }
      });
      form.find("textarea.required").each(function() {
        $(this).removeClass("error");
        if (jQuery.trim($(this).val()) === "") {
          $(this).addClass("error");
          $(this).wiggle({
            callback: (function(_this) {
              return function() {
                return $(_this).focus();
              };
            })(this)
          });
          return error_free = false;
        }
      });
      if (error_free) {
        map = {};
        opts = {
          parent: this
        };
        isFileUpload = false;
        _ref = form.find("input");
        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
          o = _ref[_i];
          if ((o.value != null) && jQuery.trim(o.value).length > 0) {
            map[o.name] = o.value;
          }
          if (o.type === "file") {
            isFileUpload = true;
          }
        }
        _ref1 = form.find("textarea");
        for (_j = 0, _len1 = _ref1.length; _j < _len1; _j++) {
          o = _ref1[_j];
          if ((o.value != null) && jQuery.trim(o.value).length > 0) {
            map[o.name] = o.value;
          }
        }
        _ref2 = form.find("select");
        for (_k = 0, _len2 = _ref2.length; _k < _len2; _k++) {
          o = _ref2[_k];
          val = this.getSelectedValue(o);
          if ((val != null) && jQuery.trim(val).length > 0) {
            map[o.name] = val;
          }
        }
        opts.responseContentType = $("div select[name=responseContentType]", $(this.el)).val();
        opts.requestContentType = $("div select[name=parameterContentType]", $(this.el)).val();
        $(".response_throbber", $(this.el)).show();
        if (isFileUpload) {
          return this.handleFileUpload(map, form);
        } else {
          return this.model["do"](map, opts, this.showCompleteStatus, this.showErrorStatus, this);
        }
      }
    };

    OperationView.prototype.success = function(response, parent) {
      return parent.showCompleteStatus(response);
    };

    OperationView.prototype.handleFileUpload = function(map, form) {
      var bodyParam, el, headerParams, o, obj, param, params, _i, _j, _k, _l, _len, _len1, _len2, _len3, _ref, _ref1, _ref2, _ref3;
      _ref = form.serializeArray();
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        o = _ref[_i];
        if ((o.value != null) && jQuery.trim(o.value).length > 0) {
          map[o.name] = o.value;
        }
      }
      bodyParam = new FormData();
      params = 0;
      _ref1 = this.model.parameters;
      for (_j = 0, _len1 = _ref1.length; _j < _len1; _j++) {
        param = _ref1[_j];
        if (param.paramType === 'form') {
          if (param.type.toLowerCase() !== 'file' && map[param.name] !== void 0) {
            bodyParam.append(param.name, map[param.name]);
          }
        }
      }
      headerParams = {};
      _ref2 = this.model.parameters;
      for (_k = 0, _len2 = _ref2.length; _k < _len2; _k++) {
        param = _ref2[_k];
        if (param.paramType === 'header') {
          headerParams[param.name] = map[param.name];
        }
      }
      _ref3 = form.find('input[type~="file"]');
      for (_l = 0, _len3 = _ref3.length; _l < _len3; _l++) {
        el = _ref3[_l];
        if (typeof el.files[0] !== 'undefined') {
          bodyParam.append($(el).attr('name'), el.files[0]);
          params += 1;
        }
      }
      this.invocationUrl = this.model.supportHeaderParams() ? (headerParams = this.model.getHeaderParams(map), this.model.urlify(map, false)) : this.model.urlify(map, true);
      $(".request_url", $(this.el)).html("<pre></pre>");
      $(".request_url pre", $(this.el)).text(this.invocationUrl);
      obj = {
        type: this.model.method,
        url: this.invocationUrl,
        headers: headerParams,
        data: bodyParam,
        dataType: 'json',
        contentType: false,
        processData: false,
        error: (function(_this) {
          return function(data, textStatus, error) {
            return _this.showErrorStatus(_this.wrap(data), _this);
          };
        })(this),
        success: (function(_this) {
          return function(data) {
            return _this.showResponse(data, _this);
          };
        })(this),
        complete: (function(_this) {
          return function(data) {
            return _this.showCompleteStatus(_this.wrap(data), _this);
          };
        })(this)
      };
      if (window.authorizations) {
        window.authorizations.apply(obj);
      }
      if (params === 0) {
        obj.data.append("fake", "true");
      }
      jQuery.ajax(obj);
      return false;
    };

    OperationView.prototype.wrap = function(data) {
      var h, headerArray, headers, i, o, _i, _len;
      headers = {};
      headerArray = data.getAllResponseHeaders().split("\r");
      for (_i = 0, _len = headerArray.length; _i < _len; _i++) {
        i = headerArray[_i];
        h = i.split(':');
        if (h[0] !== void 0 && h[1] !== void 0) {
          headers[h[0].trim()] = h[1].trim();
        }
      }
      o = {};
      o.content = {};
      o.content.data = data.responseText;
      o.headers = headers;
      o.request = {};
      o.request.url = this.invocationUrl;
      o.status = data.status;
      return o;
    };

    OperationView.prototype.getSelectedValue = function(select) {
      var opt, options, _i, _len, _ref;
      if (!select.multiple) {
        return select.value;
      } else {
        options = [];
        _ref = select.options;
        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
          opt = _ref[_i];
          if (opt.selected) {
            options.push(opt.value);
          }
        }
        if (options.length > 0) {
          return options;
        } else {
          return null;
        }
      }
    };

    OperationView.prototype.hideResponse = function(e) {
      if (e != null) {
        e.preventDefault();
      }
      $(".response", $(this.el)).slideUp();
      return $(".response_hider", $(this.el)).fadeOut();
    };

    OperationView.prototype.showResponse = function(response) {
      var prettyJson;
      prettyJson = JSON.stringify(response, null, "\t").replace(/\n/g, "<br>");
      return $(".response_body", $(this.el)).html(escape(prettyJson));
    };

    OperationView.prototype.showErrorStatus = function(data, parent) {
      return parent.showStatus(data);
    };

    OperationView.prototype.showCompleteStatus = function(data, parent) {
      return parent.showStatus(data);
    };

    OperationView.prototype.formatXml = function(xml) {
      var contexp, formatted, indent, lastType, lines, ln, pad, reg, transitions, wsexp, _fn, _i, _len;
      reg = /(>)(<)(\/*)/g;
      wsexp = /[ ]*(.*)[ ]+\n/g;
      contexp = /(<.+>)(.+\n)/g;
      xml = xml.replace(reg, '$1\n$2$3').replace(wsexp, '$1\n').replace(contexp, '$1\n$2');
      pad = 0;
      formatted = '';
      lines = xml.split('\n');
      indent = 0;
      lastType = 'other';
      transitions = {
        'single->single': 0,
        'single->closing': -1,
        'single->opening': 0,
        'single->other': 0,
        'closing->single': 0,
        'closing->closing': -1,
        'closing->opening': 0,
        'closing->other': 0,
        'opening->single': 1,
        'opening->closing': 0,
        'opening->opening': 1,
        'opening->other': 1,
        'other->single': 0,
        'other->closing': -1,
        'other->opening': 0,
        'other->other': 0
      };
      _fn = function(ln) {
        var fromTo, j, key, padding, type, types, value;
        types = {
          single: Boolean(ln.match(/<.+\/>/)),
          closing: Boolean(ln.match(/<\/.+>/)),
          opening: Boolean(ln.match(/<[^!?].*>/))
        };
        type = ((function() {
          var _results;
          _results = [];
          for (key in types) {
            value = types[key];
            if (value) {
              _results.push(key);
            }
          }
          return _results;
        })())[0];
        type = type === void 0 ? 'other' : type;
        fromTo = lastType + '->' + type;
        lastType = type;
        padding = '';
        indent += transitions[fromTo];
        padding = ((function() {
          var _j, _ref, _results;
          _results = [];
          for (j = _j = 0, _ref = indent; 0 <= _ref ? _j < _ref : _j > _ref; j = 0 <= _ref ? ++_j : --_j) {
            _results.push('  ');
          }
          return _results;
        })()).join('');
        if (fromTo === 'opening->closing') {
          return formatted = formatted.substr(0, formatted.length - 1) + ln + '\n';
        } else {
          return formatted += padding + ln + '\n';
        }
      };
      for (_i = 0, _len = lines.length; _i < _len; _i++) {
        ln = lines[_i];
        _fn(ln);
      }
      return formatted;
    };

    OperationView.prototype.showStatus = function(response) {
      var code, content, contentType, e, headers, json, opts, pre, response_body, response_body_el, url;
      if (response.content === void 0) {
        content = response.data;
        url = response.url;
      } else {
        content = response.content.data;
        url = response.request.url;
      }
      headers = response.headers;
      contentType = null;
      if (headers) {
        contentType = headers["Content-Type"] || headers["content-type"];
        if (contentType) {
          contentType = contentType.split(";")[0].trim();
        }
      }
      if (!content) {
        code = $('<code />').text("no content");
        pre = $('<pre class="json" />').append(code);
      } else if (contentType === "application/json" || /\+json$/.test(contentType)) {
        json = null;
        try {
          json = JSON.stringify(JSON.parse(content), null, "  ");
        } catch (_error) {
          e = _error;
          json = "can't parse JSON.  Raw result:\n\n" + content;
        }
        code = $('<code />').text(json);
        pre = $('<pre class="json" />').append(code);
      } else if (contentType === "application/xml" || /\+xml$/.test(contentType)) {
        code = $('<code />').text(this.formatXml(content));
        pre = $('<pre class="xml" />').append(code);
      } else if (contentType === "text/html") {
        code = $('<code />').html(_.escape(content));
        pre = $('<pre class="xml" />').append(code);
      } else if (/^image\//.test(contentType)) {
        pre = $('<img>').attr('src', url);
      } else {
        code = $('<code />').text(content);
        pre = $('<pre class="json" />').append(code);
      }
      response_body = pre;
      $(".request_url", $(this.el)).html("<pre></pre>");
      $(".request_url pre", $(this.el)).text(url);
      $(".response_code", $(this.el)).html("<pre>" + response.status + "</pre>");
      $(".response_body", $(this.el)).html(response_body);
      $(".response_headers", $(this.el)).html("<pre>" + _.escape(JSON.stringify(response.headers, null, "  ")).replace(/\n/g, "<br>") + "</pre>");
      $(".response", $(this.el)).slideDown();
      $(".response_hider", $(this.el)).show();
      $(".response_throbber", $(this.el)).hide();
      response_body_el = $('.response_body', $(this.el))[0];
      opts = this.options.swaggerOptions;
      if (opts.highlightSizeThreshold && response.data.length > opts.highlightSizeThreshold) {
        return response_body_el;
      } else {
        return hljs.highlightBlock(response_body_el);
      }
    };

    OperationView.prototype.toggleOperationContent = function() {
      var elem;
      elem = $('#' + Docs.escapeResourceName(this.model.parentId + "_" + this.model.nickname + "_content"));
      if (elem.is(':visible')) {
        return Docs.collapseOperation(elem);
      } else {
        return Docs.expandOperation(elem);
      }
    };

    return OperationView;

  })(Backbone.View);

  StatusCodeView = (function(_super) {
    __extends(StatusCodeView, _super);

    function StatusCodeView() {
      return StatusCodeView.__super__.constructor.apply(this, arguments);
    }

    StatusCodeView.prototype.initialize = function() {};

    StatusCodeView.prototype.render = function() {
      var responseModel, responseModelView, template;
      template = this.template();
      $(this.el).html(template(this.model));
      if (swaggerUi.api.models.hasOwnProperty(this.model.responseModel)) {
        responseModel = {
          sampleJSON: JSON.stringify(swaggerUi.api.models[this.model.responseModel].createJSONSample(), null, 2),
          isParam: false,
          signature: swaggerUi.api.models[this.model.responseModel].getMockSignature()
        };
        responseModelView = new SignatureView({
          model: responseModel,
          tagName: 'div'
        });
        $('.model-signature', this.$el).append(responseModelView.render().el);
      } else {
        $('.model-signature', this.$el).html('');
      }
      return this;
    };

    StatusCodeView.prototype.template = function() {
      return Handlebars.templates.status_code;
    };

    return StatusCodeView;

  })(Backbone.View);

  ParameterView = (function(_super) {
    __extends(ParameterView, _super);

    function ParameterView() {
      return ParameterView.__super__.constructor.apply(this, arguments);
    }

    ParameterView.prototype.initialize = function() {
      return Handlebars.registerHelper('isArray', function(param, opts) {
        if (param.type.toLowerCase() === 'array' || param.allowMultiple) {
          return opts.fn(this);
        } else {
          return opts.inverse(this);
        }
      });
    };

    ParameterView.prototype.render = function() {
      var contentTypeModel, isParam, parameterContentTypeView, ref, responseContentTypeView, schema, signatureModel, signatureView, template, type;
      type = this.model.type || this.model.dataType;
      if (typeof type === 'undefined') {
        schema = this.model.schema;
        if (schema && schema['$ref']) {
          ref = schema['$ref'];
          if (ref.indexOf('#/definitions/') === 0) {
            type = ref.substring('#/definitions/'.length);
          } else {
            type = ref;
          }
        }
      }
      this.model.type = type;
      this.model.paramType = this.model["in"] || this.model.paramType;
      if (this.model.paramType === 'body') {
        this.model.isBody = true;
      }
      if (type && type.toLowerCase() === 'file') {
        this.model.isFile = true;
      }
      this.model["default"] = this.model["default"] || this.model.defaultValue;
      if (this.model.allowableValues) {
        this.model.isList = true;
      }
      template = this.template();
      $(this.el).html(template(this.model));
      signatureModel = {
        sampleJSON: this.model.sampleJSON,
        isParam: true,
        signature: this.model.signature
      };
      if (this.model.sampleJSON) {
        signatureView = new SignatureView({
          model: signatureModel,
          tagName: 'div'
        });
        $('.model-signature', $(this.el)).append(signatureView.render().el);
      } else {
        $('.model-signature', $(this.el)).html(this.model.signature);
      }
      isParam = false;
      if (this.model.isBody) {
        isParam = true;
      }
      contentTypeModel = {
        isParam: isParam
      };
      contentTypeModel.consumes = this.model.consumes;
      if (isParam) {
        parameterContentTypeView = new ParameterContentTypeView({
          model: contentTypeModel
        });
        $('.parameter-content-type', $(this.el)).append(parameterContentTypeView.render().el);
      } else {
        responseContentTypeView = new ResponseContentTypeView({
          model: contentTypeModel
        });
        $('.response-content-type', $(this.el)).append(responseContentTypeView.render().el);
      }
      return this;
    };

    ParameterView.prototype.template = function() {
      if (this.model.isList) {
        return Handlebars.templates.param_list;
      } else {
        if (this.options.readOnly) {
          if (this.model.required) {
            return Handlebars.templates.param_readonly_required;
          } else {
            return Handlebars.templates.param_readonly;
          }
        } else {
          if (this.model.required) {
            return Handlebars.templates.param_required;
          } else {
            return Handlebars.templates.param;
          }
        }
      }
    };

    return ParameterView;

  })(Backbone.View);

  SignatureView = (function(_super) {
    __extends(SignatureView, _super);

    function SignatureView() {
      return SignatureView.__super__.constructor.apply(this, arguments);
    }

    SignatureView.prototype.events = {
      'click a.description-link': 'switchToDescription',
      'click a.snippet-link': 'switchToSnippet',
      'mousedown .snippet': 'snippetToTextArea'
    };

    SignatureView.prototype.initialize = function() {};

    SignatureView.prototype.render = function() {
      var template;
      template = this.template();
      $(this.el).html(template(this.model));
      this.switchToSnippet();
      this.isParam = this.model.isParam;
      if (this.isParam) {
        $('.notice', $(this.el)).text('Click to set as parameter value');
      }
      return this;
    };

    SignatureView.prototype.template = function() {
      return Handlebars.templates.signature;
    };

    SignatureView.prototype.switchToDescription = function(e) {
      if (e != null) {
        e.preventDefault();
      }
      $(".snippet", $(this.el)).hide();
      $(".description", $(this.el)).show();
      $('.description-link', $(this.el)).addClass('selected');
      return $('.snippet-link', $(this.el)).removeClass('selected');
    };

    SignatureView.prototype.switchToSnippet = function(e) {
      if (e != null) {
        e.preventDefault();
      }
      $(".description", $(this.el)).hide();
      $(".snippet", $(this.el)).show();
      $('.snippet-link', $(this.el)).addClass('selected');
      return $('.description-link', $(this.el)).removeClass('selected');
    };

    SignatureView.prototype.snippetToTextArea = function(e) {
      var textArea;
      if (this.isParam) {
        if (e != null) {
          e.preventDefault();
        }
        textArea = $('textarea', $(this.el.parentNode.parentNode.parentNode));
        if ($.trim(textArea.val()) === '') {
          return textArea.val(this.model.sampleJSON);
        }
      }
    };

    return SignatureView;

  })(Backbone.View);

  ContentTypeView = (function(_super) {
    __extends(ContentTypeView, _super);

    function ContentTypeView() {
      return ContentTypeView.__super__.constructor.apply(this, arguments);
    }

    ContentTypeView.prototype.initialize = function() {};

    ContentTypeView.prototype.render = function() {
      var template;
      template = this.template();
      $(this.el).html(template(this.model));
      $('label[for=contentType]', $(this.el)).text('Response Content Type');
      return this;
    };

    ContentTypeView.prototype.template = function() {
      return Handlebars.templates.content_type;
    };

    return ContentTypeView;

  })(Backbone.View);

  ResponseContentTypeView = (function(_super) {
    __extends(ResponseContentTypeView, _super);

    function ResponseContentTypeView() {
      return ResponseContentTypeView.__super__.constructor.apply(this, arguments);
    }

    ResponseContentTypeView.prototype.initialize = function() {};

    ResponseContentTypeView.prototype.render = function() {
      var template;
      template = this.template();
      $(this.el).html(template(this.model));
      $('label[for=responseContentType]', $(this.el)).text('Response Content Type');
      return this;
    };

    ResponseContentTypeView.prototype.template = function() {
      return Handlebars.templates.response_content_type;
    };

    return ResponseContentTypeView;

  })(Backbone.View);

  ParameterContentTypeView = (function(_super) {
    __extends(ParameterContentTypeView, _super);

    function ParameterContentTypeView() {
      return ParameterContentTypeView.__super__.constructor.apply(this, arguments);
    }

    ParameterContentTypeView.prototype.initialize = function() {};

    ParameterContentTypeView.prototype.render = function() {
      var template;
      template = this.template();
      $(this.el).html(template(this.model));
      $('label[for=parameterContentType]', $(this.el)).text('Parameter content type:');
      return this;
    };

    ParameterContentTypeView.prototype.template = function() {
      return Handlebars.templates.parameter_content_type;
    };

    return ParameterContentTypeView;

  })(Backbone.View);

  ApiKeyButton = (function(_super) {
    __extends(ApiKeyButton, _super);

    function ApiKeyButton() {
      return ApiKeyButton.__super__.constructor.apply(this, arguments);
    }

    ApiKeyButton.prototype.initialize = function() {};

    ApiKeyButton.prototype.render = function() {
      var template;
      template = this.template();
      $(this.el).html(template(this.model));
      return this;
    };

    ApiKeyButton.prototype.events = {
      "click #apikey_button": "toggleApiKeyContainer",
      "click #apply_api_key": "applyApiKey"
    };

    ApiKeyButton.prototype.applyApiKey = function() {
      var elem;
      window.authorizations.add(this.model.name, new ApiKeyAuthorization(this.model.name, $("#input_apiKey_entry").val(), this.model["in"]));
      window.swaggerUi.load();
      return elem = $('#apikey_container').show();
    };

    ApiKeyButton.prototype.toggleApiKeyContainer = function() {
      var elem;
      if ($('#apikey_container').length > 0) {
        elem = $('#apikey_container').first();
        if (elem.is(':visible')) {
          return elem.hide();
        } else {
          $('.auth_container').hide();
          return elem.show();
        }
      }
    };

    ApiKeyButton.prototype.template = function() {
      return Handlebars.templates.apikey_button_view;
    };

    return ApiKeyButton;

  })(Backbone.View);

  BasicAuthButton = (function(_super) {
    __extends(BasicAuthButton, _super);

    function BasicAuthButton() {
      return BasicAuthButton.__super__.constructor.apply(this, arguments);
    }

    BasicAuthButton.prototype.initialize = function() {};

    BasicAuthButton.prototype.render = function() {
      var template;
      template = this.template();
      $(this.el).html(template(this.model));
      return this;
    };

    BasicAuthButton.prototype.events = {
      "click #basic_auth_button": "togglePasswordContainer",
      "click #apply_basic_auth": "applyPassword"
    };

    BasicAuthButton.prototype.applyPassword = function() {
      var elem, password, username;
      console.log("applying password");
      username = $(".input_username").val();
      password = $(".input_password").val();
      window.authorizations.add(this.model.type, new PasswordAuthorization("basic", username, password));
      window.swaggerUi.load();
      return elem = $('#basic_auth_container').hide();
    };

    BasicAuthButton.prototype.togglePasswordContainer = function() {
      var elem;
      if ($('#basic_auth_container').length > 0) {
        elem = $('#basic_auth_container').show();
        if (elem.is(':visible')) {
          return elem.slideUp();
        } else {
          $('.auth_container').hide();
          return elem.show();
        }
      }
    };

    BasicAuthButton.prototype.template = function() {
      return Handlebars.templates.basic_auth_button_view;
    };

    return BasicAuthButton;

  })(Backbone.View);

}).call(this);
