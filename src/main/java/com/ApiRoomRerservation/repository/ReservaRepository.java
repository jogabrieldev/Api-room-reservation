package com.ApiRoomRerservation.repository;

import com.ApiRoomRerservation.entity.Reserva;
import com.ApiRoomRerservation.entity.StatusReserva;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @Query("""
            select (count(r) > 0) from Reserva r
            where r.sala.id = :salaId
              and r.status <> :statusIgnorado
              and r.inicio < :fim
              and r.fim > :inicio
            """)
    boolean existeConflito(@Param("salaId") Long salaId,
                           @Param("inicio") LocalDateTime inicio,
                           @Param("fim") LocalDateTime fim,
                           @Param("statusIgnorado") StatusReserva statusIgnorado);

    List<Reserva> findByUsuarioIdOrderByInicioDesc(UUID usuarioId);

    List<Reserva> findByUsuarioIdAndStatusOrderByInicioDesc(UUID usuarioId, StatusReserva status);

    List<Reserva> findBySalaIdOrderByInicioDesc(Long salaId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Reserva r where r.id = :id")
    Optional<Reserva> buscarPorIdComBloqueio(@Param("id") Long id);
}
