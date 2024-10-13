package vn.ptit.service;

import vn.ptit.model.Esp32Config;

public interface IEsp32Service {

    Esp32Config getEsp32Config();

    boolean updateEsp32Config(Esp32Config esp32Config);
}
