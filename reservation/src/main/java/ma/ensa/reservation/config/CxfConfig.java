package ma.ensa.reservation.config;

import lombok.AllArgsConstructor;
import ma.ensa.reservation.ws.ReservationSoapService;
import ma.ensa.reservation.ws.ClientSoapService;
import ma.ensa.reservation.ws.ChambreSoapService;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CxfConfig {

    @Bean
    public ServletRegistrationBean<CXFServlet> cxfServlet() {
        return new ServletRegistrationBean<>(new CXFServlet(), "/services/*");
    }

    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        return new SpringBus();
    }

    @Bean
    public EndpointImpl reservationServiceEndpoint(ReservationSoapService reservationSoapService) {
        EndpointImpl endpoint = new EndpointImpl(springBus(), reservationSoapService);
        endpoint.publish("/reservation");
        return endpoint;
    }

    @Bean
    public EndpointImpl clientServiceEndpoint(ClientSoapService clientSoapService) {
        EndpointImpl endpoint = new EndpointImpl(springBus(), clientSoapService);
        endpoint.publish("/client");
        return endpoint;
    }

    @Bean
    public EndpointImpl chambreServiceEndpoint(ChambreSoapService chambreSoapService) {
        EndpointImpl endpoint = new EndpointImpl(springBus(), chambreSoapService);
        endpoint.publish("/chambre");
        return endpoint;
    }
}
