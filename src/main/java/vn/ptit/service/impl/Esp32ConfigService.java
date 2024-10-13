package vn.ptit.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.ptit.model.Esp32Config;
import vn.ptit.repository.Esp32ConfigRepository;
import vn.ptit.service.IEsp32Service;

@Service
public class Esp32ConfigService implements IEsp32Service {

    @Autowired
    private Esp32ConfigRepository esp32ConfigRepository;

    @Override
    public Esp32Config getEsp32Config() {
        return esp32ConfigRepository.findAll().get(0);
    }

    @Override
    public boolean updateEsp32Config(Esp32Config esp32Config) {
        return esp32ConfigRepository.save(esp32Config).getId() > 0;
    }
}
