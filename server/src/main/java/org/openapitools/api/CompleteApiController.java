package org.openapitools.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-03-14T12:05:55.435057+02:00[Africa/Cairo]")

@Controller
@RequestMapping("${openapi.miniSearchEngine.base-path:}")
public class CompleteApiController implements CompleteApi {

    private final NativeWebRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public CompleteApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

}
