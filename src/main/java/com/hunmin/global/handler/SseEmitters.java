package com.hunmin.global.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
@Component
@RequiredArgsConstructor
@Log4j2
public class SseEmitters {
    private final ConcurrentHashMap<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    //SseEmitter 생성
    public SseEmitter create(String sseId, SseEmitter emitter) {
        emitters.put(sseId, emitter);
        return emitter;
    }

    //SseEmitter 찾기
    public SseEmitter findSingleEmitter(String sseId) {
        return emitters.get(sseId);
    }

    //sseId 별 SseEmitter 찾기
    public Map<String, SseEmitter> findEmitter(String sseId){
        return emitters.entrySet().stream().filter(entry -> entry.getKey().startsWith(sseId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    //sseEmitter 삭제
    public void delete(String sseId) {
        emitters.remove(sseId);
    }
}
