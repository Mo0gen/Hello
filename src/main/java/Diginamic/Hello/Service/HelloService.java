package Diginamic.Hello.Service;

import org.springframework.stereotype.Service;

@Service
public class HelloService {

    public String salutations() {
        return "Je suis la classe de service";
    }
}