package com.ApiRoomRerservation.repository;

import com.ApiRoomRerservation.entity.Sala;
import com.ApiRoomRerservation.entity.StatusReserva;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SalaRepository extends JpaRepository<Sala, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Sala s where s.id = :id")
    Optional<Sala> buscarPorIdComBloqueio(@Param("id") Long id);

    @Query("""
            select s from Sala s
            where s.ativa = true
              and not exists (
                select r.id from Reserva r
                where r.sala = s
                  and r.status <> :statusIgnorado
                  and r.inicio < :fim
                  and r.fim > :inicio
              )
            order by s.nome
            """)
    List<Sala> buscarDisponiveis(@Param("inicio") LocalDateTime inicio,
                                 @Param("fim") LocalDateTime fim,
                                 @Param("statusIgnorado") StatusReserva statusIgnorado);
}
