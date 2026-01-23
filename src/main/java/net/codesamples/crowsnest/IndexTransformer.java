package net.codesamples.crowsnest;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.resource.ResourceTransformer;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;

import java.io.IOException;

@Service
public class IndexTransformer implements ResourceTransformer {

    @Value("${ws.hostname.port}")
    private String wsHostnamePort;

    @Override
    public Resource transform(HttpServletRequest request,
                              Resource resource,
                              ResourceTransformerChain chain) throws IOException {
        String html = IOUtils.toString(resource.getInputStream(), "utf-8");
        html = html.replace("<!--WS_HOST_HERE-->",
                "<script>var wsHostname='" + wsHostnamePort + "'</script>");
        return new TransformedResource(resource, html.getBytes());
    }
}
