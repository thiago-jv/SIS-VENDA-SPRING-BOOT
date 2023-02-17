package com.venda;

import com.venda.config.jwt.JwtService;
import com.venda.domain.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.logging.Logger;

@SpringBootApplication
public class VendaApplication {

    @Autowired
    JwtService service;

    static Logger logger = Logger.getLogger(String.valueOf(VendaApplication.class));

    public static void main(String[] args) {
        ConfigurableApplicationContext contex = SpringApplication.run(VendaApplication.class, args);
        JwtService service = contex.getBean(JwtService.class);
        Usuario usuario = Usuario.builder().login("java").build();
        String token = service.gerarToken(usuario);
        logger.info(token);
        boolean isTokenValido = service.tokenValido(token);
        logger.info("O token está válido? " + isTokenValido);
        logger.info(service.obterLoginUsuario(token));

    }

}
