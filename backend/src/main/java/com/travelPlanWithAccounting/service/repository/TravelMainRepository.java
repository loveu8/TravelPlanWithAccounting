package com.travelPlanWithAccounting.service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.travelPlanWithAccounting.service.entity.TravelMain;
import com.travelPlanWithAccounting.service.repository.projection.PopularTravelAggregate;

@Repository
public interface TravelMainRepository extends JpaRepository<TravelMain, UUID>  {
    List<TravelMain> findByMemberId(UUID memberId);

    @Query(
        """
        select new com.travelPlanWithAccounting.service.repository.projection.PopularTravelAggregate(
            tm.id,
            tm.title,
            tm.startDate,
            tm.endDate,
            tm.visitPlace,
            tm.isPrivate,
            tm.createdAt,
            count(tf)
        )
        from TravelMain tm
        left join TravelFav tf on tf.travelMain = tm
        where tm.isPrivate = false
        group by tm.id, tm.title, tm.startDate, tm.endDate, tm.visitPlace, tm.isPrivate, tm.createdAt
        order by count(tf) desc, tm.createdAt desc
        """
    )
    List<PopularTravelAggregate> findPopularPublicTravels();
}
