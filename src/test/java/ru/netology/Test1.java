package ru.netology;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import ru.netology.entity.Country;
import ru.netology.entity.Location;

import ru.netology.geo.GeoService;
import ru.netology.geo.GeoServiceImpl;

import ru.netology.i18n.LocalizationServiceImpl;
import ru.netology.sender.MessageSenderImpl;

import java.util.HashMap;
import java.util.Map;

public class Test1 {

    @Test
    public void geoServiceImplTest() {
        GeoService geoService = new GeoServiceImpl();
        Location location = geoService.byIp("127.0.0.1");
        Assertions.assertEquals(location.getStreet(), null);

        location = geoService.byIp(GeoServiceImpl.NEW_YORK_IP);
        Assertions.assertEquals(location.getStreet(), "10th Avenue");

        location = geoService.byIp(GeoServiceImpl.MOSCOW_IP);
        Assertions.assertEquals(location.getStreet(), "Lenina");

        location = geoService.byIp("172.1.33.12");
        Assertions.assertEquals(location.getCountry(), Country.RUSSIA);

        location = geoService.byIp("96.45.185.150");
        Assertions.assertEquals(location.getCountry(), Country.USA);
    }

    @Test
    public void localizationServiceImplTest(){

        LocalizationServiceImpl localizationService = new LocalizationServiceImpl();
        String message = localizationService.locale(Country.RUSSIA);
        Assertions.assertEquals(message, "Добро пожаловать");

        message = localizationService.locale(Country.BRAZIL);
        Assertions.assertEquals(message, "Welcome");
    }

    @Test
    public void messageSenderImpleTest(){
        GeoServiceImpl geoServiceMock = Mockito.mock(GeoServiceImpl.class);
        Mockito
                .when(geoServiceMock.byIp("172.123.12.19"))
                .thenReturn(new Location("Moscow", Country.RUSSIA, null, 0));
        Mockito
                .when(geoServiceMock.byIp("172.0.32.11"))
                .thenReturn(new Location("Moscow", Country.RUSSIA, "Lenina", 15));
        Mockito
                .when(geoServiceMock.byIp("96.123.12.19"))
                .thenReturn(new Location("New York", Country.USA, null,  0));
        Mockito
                .when(geoServiceMock.byIp("96.44.183.149"))
                .thenReturn(new Location("New York", Country.USA, "10th Avenue", 32));

        LocalizationServiceImpl localizationServiceMock = Mockito.mock(LocalizationServiceImpl.class);
        Mockito
                .when(localizationServiceMock.locale(Country.RUSSIA))
                .thenReturn("Добро пожаловать");
        Mockito
                .when(localizationServiceMock.locale(Country.USA))
                .thenReturn("Welcome");

        MessageSenderImpl messageSender = new MessageSenderImpl(geoServiceMock, localizationServiceMock);

        Map<String, String> headers = new HashMap<String, String>();

        headers.put(MessageSenderImpl.IP_ADDRESS_HEADER, "172.123.12.19");
        Assertions.assertEquals(messageSender.send(headers), "Добро пожаловать");
        System.out.println();

        headers.put(MessageSenderImpl.IP_ADDRESS_HEADER, "172.0.32.11");
        Assertions.assertEquals(messageSender.send(headers), "Добро пожаловать");
        System.out.println();

        headers.put(MessageSenderImpl.IP_ADDRESS_HEADER, "96.123.12.19");
        Assertions.assertEquals(messageSender.send(headers), "Welcome");
        System.out.println();

        headers.put(MessageSenderImpl.IP_ADDRESS_HEADER, "96.44.183.149");
        Assertions.assertEquals(messageSender.send(headers), "Welcome");
    }
}
